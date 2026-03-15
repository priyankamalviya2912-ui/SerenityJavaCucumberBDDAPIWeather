![Java](https://img.shields.io/badge/Java-17-blue) ![Serenity](https://img.shields.io/badge/Serenity_BDD-5.0.4-brightgreen) ![Status](https://img.shields.io/badge/Roadmap-Active-blue)

---

<div align="center">

# Framework Roadmap

## Serenity BDD + Cucumber + RestAssured — Weatherbit API Automation

</div>

---

This document outlines the planned improvements and future development directions for the Weatherbit API test automation framework. Items are grouped by phase and ordered by priority within each phase.

---

## Phase 1 — Security & Accuracy (Immediate)

### 1.1 Externalize API Key via Environment Variable

**Current state:**

The Weatherbit API key is stored in plain text inside `serenity.conf`, which is committed to source control:

```hocon
api.key = "your-api-key-here"
```

This is a security risk — anyone with repository access can see and misuse the key.

**Planned change:**

Remove the hard-coded key from `serenity.conf` and replace it with an environment variable reference:

```hocon
environments {
    default {
        restapi.weather.baseurl = "http://api.weatherbit.io/v2.0"
        api.key                 = ${?WEATHERBIT_API_KEY}
    }
}
```

The key will be set as an OS-level or CI-level secret:

```bash
# Local machine (set once)
export WEATHERBIT_API_KEY=your-api-key-here

# Run tests — key is injected at runtime, never in code
mvn clean verify -Dcucumber.filter.tags="@api"
```

**Why no encryption?**
Encrypting a secret that still lives in the repository only adds complexity — it does not eliminate the risk. The correct approach is to keep secrets out of source code entirely by externalising them to the environment.

**Impact:** Eliminates committed credentials. Enables safe use in CI/CD pipelines where secrets are stored as pipeline variables.

---

### 1.2 Fix AC4 — State Representation Accuracy (`Map<String, List<String>>`)

**Current state:**

`FileUtils.getUsStateCityIdsFromCsv()` uses a `Map<String, String>` where the state code maps to a single city ID:

```java
Map<String, String> stateMap = new HashMap<>();
stateMap.put(line[2], line[0]); // state_code -> one city_id only
```

If the CSV contains multiple cities for the same state (e.g. New York has New York City, Buffalo, Albany), only whichever city appears **first** in the file is kept. The result is that the "coldest state" finding depends on CSV ordering, not actual weather data — making the test result unreliable.

**Planned change:**

Change to `Map<String, List<String>>` to collect **all city IDs per state**, then pass all of them to the API:

```java
Map<String, List<String>> stateMap = new HashMap<>();
stateMap.computeIfAbsent(line[2], k -> new ArrayList<>()).add(line[0]);
```

Then in the `Then` step, group the API results by state, find the minimum temperature city within each state, and compare across states — so the coldest state finding reflects real data from all available cities in that state.

**Impact:** AC4 becomes genuinely accurate rather than an artifact of CSV row ordering. The report correctly identifies the coldest state based on full coverage.

---

## Phase 2 — Test Coverage (Short Term)

### 2.1 Negative and Error Scenario Coverage

**Current state:**

All four scenarios test the happy path only — valid city IDs, valid coordinates, well-formed files, active API key. There are no tests verifying the framework and API behave correctly under error conditions.

**Planned scenarios:**

| Scenario | Input | Expected Outcome |
|---|---|---|
| Invalid city ID | Non-existent city ID | Status 400 or empty `data` array |
| Invalid coordinates | Coordinates out of range | Status 400 |
| Missing API key | Request without `key` param | Status 401 Unauthorized |
| API rate limit exceeded | Exceed free tier request limit | Status 429 Too Many Requests |
| Empty city list | Empty string passed as cities param | Status 400 or meaningful error |

**Feature file additions:**

```gherkin
@negative
Scenario: AC5_Return error for invalid city ID
  When I request weather for an invalid city ID "99999999"
  Then the response should return status code 400

@negative
Scenario: AC6_Return unauthorised when API key is missing
  When I request weather without an API key
  Then the response should return status code 401
```

**Impact:** Validates that the framework handles real-world failure modes. Prevents false confidence from happy-path-only coverage.

---

### 2.2 Boundary and Edge Case Testing

**Current state:**

AC2 tests one fixed coordinate pair. There is no coverage of edge cases for coordinate-based lookups.

**Planned scenarios:**

| Scenario | Input | Purpose |
|---|---|---|
| North Pole coordinates | lat=90.0, lon=0.0 | Extreme latitude boundary |
| Antimeridian longitude | lat=0.0, lon=180.0 | Extreme longitude boundary |
| Zero coordinates | lat=0.0, lon=0.0 | Null Island — valid but unusual |
| Maximum city ID list | 50 city IDs joined | Verify API handles large input |

**Impact:** Ensures the API client handles edge inputs gracefully and documents the API's known boundaries.

---

## Phase 3 — Contract & Schema Validation (Medium Term)

### 3.1 JSON Schema Validation

**Current state:**

Tests assert on specific field values (city name, temperature, count) but do not validate the **structure** of the response. If Weatherbit changes their response shape — renames a field, removes a field, changes a data type — the tests will either silently pass with incorrect data or produce confusing NullPointerExceptions rather than a clear contract failure.

**Planned change:**

Add JSON Schema files under `src/test/resources/schemas/` and validate every response against the schema before asserting on values:

```java
// In verify_response() — validate structure first, then values
response.then()
        .body(matchesJsonSchemaInClasspath("schemas/weather-current-response.json"));
```

**Example schema (`weather-current-response.json`):**

```json
{
  "$schema": "http://json-schema.org/draft-07/schema",
  "type": "object",
  "required": ["data", "count"],
  "properties": {
    "count": { "type": "integer" },
    "data": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["city_name", "temp", "weather"],
        "properties": {
          "city_name":  { "type": "string" },
          "temp":       { "type": "number" },
          "state_code": { "type": "string" },
          "weather": {
            "type": "object",
            "required": ["description"],
            "properties": {
              "description": { "type": "string" }
            }
          }
        }
      }
    }
  }
}
```

**Impact:** Tests become contract tests — they detect breaking API changes immediately, independently of value assertions. Schema files also serve as living documentation of the expected response structure.

---

## Phase 4 — Resilience (Medium Term)

### 4.1 Retry Mechanism for Flaky API Calls

**Current state:**

If the Weatherbit API returns a transient error (network timeout, 503 Service Unavailable, rate limit spike), the test fails immediately with no retry. This leads to false failures in CI pipelines that have nothing to do with the test logic.

**Planned change:**

Add a configurable retry wrapper in `WeatherApi` using RestAssured's built-in polling or a lightweight retry utility:

```java
// Retry up to 3 times with 2 second intervals on 5xx or timeout
@Step("Fetching current weather for city IDs: {0}")
public Response getWeatherByCityIds(String cityIdList) {
    return Failsafe.with(retryPolicy)
            .get(() -> SerenityRest.given()
                    .queryParam("cities", cityIdList)
                    .when().get("/current"));
}
```

Retry behaviour will be driven by `serenity.conf`:

```hocon
api.retry.attempts  = 3
api.retry.delay.ms  = 2000
```

**Impact:** Reduces false failures from transient network issues. Makes the test suite more reliable in CI environments.

---

## Phase 5 — CI/CD Pipeline (Medium Term)

### 4.2 Batch Processing for Large City ID Requests

**Current state:**

`WeatherApi.getWeatherByCityIds()` sends all city IDs in a single API call as a comma-separated query parameter:

```java
SerenityRest.given()
        .queryParam("cities", cityIdList)  // e.g. "2147714,5128581,2643743,..."
        .when().get("/current");
```

The Weatherbit API enforces a limit of **20 city IDs per request**. If more than 20 IDs are passed (which can happen in AC4 when `Map<String, List<String>>` is implemented and collects all cities across 30+ US states), the API silently truncates or rejects the excess — meaning some cities are never queried and the results are incomplete without any error being raised.

**Why this matters for AC4 specifically:**

Once AC4 is improved to use `Map<String, List<String>>` (roadmap item 1.2), the number of city IDs passed to the API will grow significantly — potentially 60–100 IDs for 30 states with multiple cities each. Without batching, the request will silently drop cities beyond the 20-ID limit.

**Planned change:**

Add a `getBatchedWeatherByCityIds()` method in `WeatherApi` that transparently partitions the city ID list into chunks of 20, makes one API call per chunk, and merges all `CityWeather` results into a single combined list:

```java
private static final int BATCH_SIZE = 20;

@Step("Fetching current weather in batches for {0} city IDs")
public List<CityWeather> getBatchedWeatherByCityIds(List<String> allCityIds) {
    List<CityWeather> allResults = new ArrayList<>();
    List<List<String>> batches = partition(allCityIds, BATCH_SIZE);

    log.info("Sending {} city IDs in {} batch(es) of up to {}",
            allCityIds.size(), batches.size(), BATCH_SIZE);

    for (int i = 0; i < batches.size(); i++) {
        String joinedIds = String.join(",", batches.get(i));
        log.debug("Batch {}/{}: {}", i + 1, batches.size(), joinedIds);
        Response batchResponse = SerenityRest.given()
                .queryParam("cities", joinedIds)
                .when().get("/current");
        batchResponse.then().statusCode(200);
        allResults.addAll(batchResponse.jsonPath().getList("data", CityWeather.class));
    }

    log.info("Total cities retrieved across all batches: {}", allResults.size());
    return allResults;
}

private static <T> List<List<T>> partition(List<T> list, int size) {
    List<List<T>> partitions = new ArrayList<>();
    for (int i = 0; i < list.size(); i += size) {
        partitions.add(list.subList(i, Math.min(i + size, list.size())));
    }
    return partitions;
}
```

**How the step definitions change:**

The batching is **transparent to the Gherkin layer** — the feature file and step text do not change. Only the step method switches from calling the single-call method to the batched one when the list exceeds the threshold:

```java
@When("I identify the coldest US state using the metadata in {string}")
public void identifyColdestFromCSV(String fileName) throws IOException, CsvValidationException {
    List<String> cityIds = FileUtils.getUsStateCityIdsFromCsv(fileName, 30);
    log.info("Total city IDs to query: {}", cityIds.size());

    if (cityIds.size() > BATCH_SIZE) {
        log.info("City ID count exceeds batch limit — switching to batched requests");
        combinedWeatherList = weatherApi.getBatchedWeatherByCityIds(cityIds);
    } else {
        response = weatherApi.getWeatherByCityIds(String.join(",", cityIds));
    }
}
```

**Serenity report visibility:**

Each batch call is a separate `@Step`, so the Serenity report shows every batch individually — the request, the response, and how many cities were returned per batch — giving full transparency into what was queried.

**Impact:** Removes the silent data loss that occurs when more than 20 city IDs are passed in one request. AC4 and any future large-scale scenarios work correctly regardless of how many cities are collected from test data files. The batching logic is centralised in `WeatherApi` and invisible to feature files and step definitions.

---

## Phase 5 — CI/CD Pipeline (Medium Term)

### 5.1 Automated Pipeline with Bitbucket Pipelines

**Current state:**

Tests are run manually via `mvn clean verify -Dcucumber.filter.tags="@api"` or the `run-api-tests.bat` script. There is no automated trigger on code changes.

**Planned pipeline (`bitbucket-pipelines.yml`):**

```yaml
image: maven:3.9.6-eclipse-temurin-17

definitions:
  steps:
    - step: &run-api-tests
        name: Run Weatherbit API Tests
        caches:
          - maven
        script:
          - mvn clean verify -Dcucumber.filter.tags="@api"
        artifacts:
          - target/site/serenity/**

pipelines:
  default:
    - step: *run-api-tests

  branches:
    main:
      - step: *run-api-tests

  pull-requests:
    '**':
      - step: *run-api-tests

  custom:
    scheduled-daily-run:
      - step: *run-api-tests
```

**How to store the API key as a Bitbucket secret:**

1. Go to **Repository Settings → Pipelines → Repository Variables**
2. Add a variable:
   - Name: `WEATHERBIT_API_KEY`
   - Value: your API key
   - Check **Secured** so it is masked in logs and never visible in plain text
3. The variable is automatically injected as an environment variable at pipeline runtime

In `serenity.conf` (from roadmap item 1.1):
```hocon
api.key = ${?WEATHERBIT_API_KEY}
```

Serenity reads it at runtime from the environment — no key ever appears in code or pipeline config.

**Pipeline triggers:**

| Trigger | Pipeline Block | When it runs |
|---|---|---|
| Any branch push | `default` | Every commit pushed to any branch |
| Push to `main` | `branches: main` | Every merge or direct push to main |
| Pull request opened/updated | `pull-requests: **` | Every PR against any branch |
| Scheduled daily | `custom: scheduled-daily-run` | Configured via **Repository Settings → Pipelines → Schedules** in Bitbucket UI |

**Serenity report as a build artifact:**

The `artifacts` block under the step preserves the full `target/site/serenity/` directory after each pipeline run. It can be downloaded directly from the Bitbucket Pipelines UI for any build — pass or fail.

**Maven cache:**

The `maven` cache entry caches the local `.m2` repository between pipeline runs, avoiding re-downloading all dependencies on every build and significantly reducing pipeline run time.

**Impact:** Every push and every pull request is validated automatically. The Serenity report is always available as a downloadable artifact from Bitbucket. The API key is stored securely as a repository secret and never committed to source control.

---

## Roadmap Summary

| Phase | Item | Effort | Impact |
|---|---|---|---|
| 1 | Externalize API key via environment variable | Low | High |
| 1 | AC4 — `Map<String, List<String>>` per state | Medium | High |
| 2 | Negative and error scenario coverage | Medium | High |
| 2 | Boundary and edge case testing | Low | Medium |
| 3 | JSON Schema validation | Medium | High |
| 4 | Retry mechanism for transient failures | Medium | Medium |
| 4 | Batch processing for city ID requests > 20 | Medium | High |
| 5 | CI/CD pipeline with Bitbucket Pipelines | Medium | High |
