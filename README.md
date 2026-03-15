![Java](https://img.shields.io/badge/Java-17-blue) ![Serenity](https://img.shields.io/badge/Serenity_BDD-5.0.4-brightgreen) ![Cucumber](https://img.shields.io/badge/Cucumber-7.33.0-green) ![RestAssured](https://img.shields.io/badge/RestAssured-5.x-orange) ![API](https://img.shields.io/badge/API-Weatherbit-blue)

---

<div align="center">

# Serenity BDD + Cucumber + RestAssured

## REST API Test Automation Framework — Weatherbit Current Weather API

</div>

---

Welcome to the **Serenity BDD API Automation Framework**. This framework uses [Serenity BDD](https://serenity-bdd.info/), [Cucumber](https://cucumber.io/), and [RestAssured](https://rest-assured.io/) to deliver clean, readable, and maintainable REST API tests in Java.

Tests are written in **Gherkin** and serve as living documentation that stays in sync with actual test results. The framework validates the [Weatherbit Current Weather API](https://www.weatherbit.io/api) across multiple real-world scenarios including city lookup, coordinate-based search, and data-driven analysis from JSON and CSV files.

---

## Table of Contents

- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [API Configuration](#api-configuration)
- [API Under Test](#api-under-test)
- [Scenarios](#scenarios)
  - [AC1 — Weather by City IDs (DataTable)](#ac1--retrieve-weather-for-multiple-cities-datatable)
  - [AC2 — Weather by Coordinates](#ac2--retrieve-weather-by-coordinates)
  - [AC3 — Warmest Australian Capital (JSON file)](#ac3--identify-warmest-australian-capital-json-file)
  - [AC4 — Coldest US State (CSV file)](#ac4--identify-coldest-us-state-csv-file)
- [POJO Classes](#pojo-classes)
- [Utility Classes](#utility-classes)
  - [FileUtils](#fileutils)
  - [ReportUtils](#reportutils)
- [BDD Feature File](#bdd-feature-file)
- [Step Definitions](#step-definitions)
- [Test Runner](#test-runner)
- [Test Data](#test-data)
- [Executing Tests](#executing-tests)
- [Reports](#reports)
- [Best Practices](#best-practices)

---

## Technology Stack

| Tool / Library        | Version      | Purpose                                     |
|-----------------------|--------------|---------------------------------------------|
| Java                  | 17           | Programming language                        |
| Maven                 | 3.x          | Build and dependency management             |
| Serenity BDD          | 5.0.4        | Test orchestration and HTML reporting       |
| Cucumber              | 7.33.0       | BDD feature file parsing and execution      |
| Serenity REST Assured | 5.0.4        | RestAssured wrapper with Serenity logging   |
| JUnit Platform        | 6.0.1        | Test runner platform (Suite API)            |
| Jackson Databind      | 2.17.0       | JSON deserialization into POJOs             |
| OpenCSV               | 5.x          | CSV file parsing for test data              |
| Hamcrest              | via Serenity | Fluent assertion matchers                   |

---

## Project Structure

```
CucumberBDDSerenityJavaSeleniumUIAndApi-main/
├── pom.xml
├── serenity.properties
├── run-api-tests.bat                             # One-click script to run API tests
└── src/
    └── test/
        ├── java/
        │   ├── api/
        │   │   ├── WeatherApi.java               # RestAssured action class (GET by city IDs / coords)
        │   │   ├── hooks/
        │   │   │   └── ApiHooks.java             # Cucumber hooks (Before/After)
        │   │   ├── steps/
        │   │   │   └── ApiStepDefinition.java    # Cucumber step definitions for all API scenarios
        │   │   ├── runner/
        │   │   │   └── ApiCucumberTestSuite.java # API test runner
        │   │   └── pojo/
        │   │       └── weather/
        │   │           ├── CityWeather.java      # POJO — maps city weather response fields
        │   │           └── WeatherDetail.java    # POJO — maps nested weather description
        │   └── utils/
        │       ├── FileUtils.java                # Reads city IDs from JSON and CSV test data files
        │       └── ReportUtils.java              # Builds formatted report strings for Serenity
        └── resources/
            ├── serenity.conf                     # Environment config and base URLs
            ├── junit-platform.properties         # Parallel execution settings
            ├── logback-test.xml                  # Logging configuration
            ├── features/
            │   └── weather/
            │       └── weather.api.feature       # BDD scenarios for Weatherbit API
            └── testdata/
                ├── ausCapCities.json             # Australian capital city IDs (AC3)
                └── us_cities.csv                 # US city metadata with state codes (AC4)
```

---

## API Configuration

All base URLs and API keys are defined centrally in `src/test/resources/serenity.conf`. Nothing is hard-coded in test or action classes.

```hocon
environments {
    default {
        restapi.weather.baseurl = "http://api.weatherbit.io/v2.0"
        api.key                 = "<your-weatherbit-api-key>"
    }
}
```

| Config Key                 | Value                              | Used By       |
|----------------------------|------------------------------------|---------------|
| `restapi.weather.baseurl`  | `http://api.weatherbit.io/v2.0`   | `WeatherApi`  |
| `api.key`                  | Weatherbit API key                 | `WeatherApi`  |

---

## API Under Test

### Weatherbit Current Weather API

| Property    | Value                              |
|-------------|------------------------------------|
| Base URL    | `http://api.weatherbit.io/v2.0`   |
| Endpoint    | `/current`                         |
| HTTP Method | `GET`                              |
| Auth        | API key via query parameter        |
| Config Key  | `restapi.weather.baseurl`          |
| Action Class| `api.WeatherApi`                   |
| Cucumber Tag| `@api`                             |

**`WeatherApi.java`** wraps all RestAssured calls using Serenity's `SerenityRest` so every request and response is automatically captured in the Serenity report.

```java
@Step("Fetching current weather for city IDs: {0}")
public Response getWeatherByCityIds(String cityIdList) {
    response = SerenityRest.given()
            .queryParam("cities", cityIdList)
            .when().get("/current");
    return response;
}

@Step("Fetching current weather by coordinates: {0}")
public Response getWeatherByCoords(Double lat, Double lon) {
    response = SerenityRest.given()
            .queryParam("lat", lat)
            .queryParam("lon", lon)
            .when().get("/current");
    return response;
}
```

---

## Scenarios

### AC1 — Retrieve Weather for Multiple Cities (DataTable)

| Tag           | `@AC1 @cityids`                                                   |
|---------------|-------------------------------------------------------------------|
| Input         | Cucumber DataTable with city IDs and expected city names          |
| API Call      | `GET /current?cities=2147714,5128581,2643743,1850147`             |
| Validates     | Status 200, all expected cities present in response               |
| Reports       | Full weather table (city / temp / condition) logged to Serenity   |

**What it does:**
1. Reads city IDs and expected city names from a Cucumber DataTable.
2. Joins city IDs as a comma-separated string and calls the API.
3. Deserializes the response `data` array into `List<CityWeather>`.
4. Checks that every expected city name is present in the response.
5. If any are missing, logs them under "MISSING CITIES DETECTED" in the report.
6. Logs the full weather table using `ReportUtils.generateWeatherTable()`.

---

### AC2 — Retrieve Weather by Coordinates

| Tag           | `@AC2 @coordinates`                                               |
|---------------|-------------------------------------------------------------------|
| Input         | Latitude and Longitude from the Gherkin step                      |
| API Call      | `GET /current?lat=-33.865143&lon=151.209900`                      |
| Validates     | Status 200, exactly 1 result, `count == 1`                        |
| Reports       | Location / Temp / Condition logged to Serenity                    |

**What it does:**
1. Passes `lat` and `lon` as query parameters.
2. Asserts response contains exactly 1 data record using Hamcrest `hasSize(1)`.
3. Logs the result using `ReportUtils.buildCoordinateReport()`.

---

### AC3 — Identify Warmest Australian Capital (JSON File)

| Tag           | `@AC3 @warmest`                                                   |
|---------------|-------------------------------------------------------------------|
| Input         | `ausCapCities.json` — JSON file with Australian capital city IDs  |
| API Call      | `GET /current?cities=<joined-ids-from-file>`                      |
| Validates     | Identifies the city with the maximum temperature                  |
| Reports       | Warmest city name, temp, and condition logged to Serenity         |

**What it does:**
1. `FileUtils.getCityIdsFromJson("ausCapCities.json")` reads and joins city IDs from the JSON file.
2. Calls the API with the joined IDs.
3. Uses Java Streams `.max(Comparator.comparing(CityWeather::getTemp))` to find the warmest city.
4. Logs the result using `ReportUtils.buildWarmestCityReport()`.

---

### AC4 — Identify Coldest US State (CSV File)

| Tag           | `@AC4 @coldest`                                                   |
|---------------|-------------------------------------------------------------------|
| Input         | `us_cities.csv` — CSV file with city metadata including state codes|
| API Call      | `GET /current?cities=<ids-of-up-to-30-US-states>`                 |
| Validates     | Identifies the city/state with the minimum temperature            |
| Reports       | State code, city, temperature, and condition logged to Serenity   |

**What it does:**
1. `FileUtils.getUsStateCityIdsFromCsv("us_cities.csv", 30)` parses the CSV, collecting one representative city per US state (up to 30).
2. Calls the API with the collected city IDs.
3. Uses Java Streams `.min(Comparator.comparing(CityWeather::getTemp))` to find the coldest city.
4. Extracts the state code from the response using JsonPath.
5. Logs the result using `ReportUtils.buildColdestStateReport()`.

---

## POJO Classes

API responses are deserialized into typed Java POJOs using Jackson. All POJOs use `@JsonIgnoreProperties(ignoreUnknown = true)` to safely ignore extra fields in the response.

| Class              | Package              | Fields                                       |
|--------------------|----------------------|----------------------------------------------|
| `CityWeather.java` | `api.pojo.weather`   | `city_name`, `temp`, `state_code`, `weather` |
| `WeatherDetail.java`| `api.pojo.weather`  | `description`                                |

**`CityWeather.java` (key fields):**
```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityWeather {

    @JsonProperty("city_name")
    private String cityName;

    private float temp;

    @JsonProperty("state_code")
    private String stateCode;

    private WeatherDetail weather;

    // getters and setters
}
```

The `weather` field maps the nested `weather` object in the response:
```json
{
  "city_name": "Sydney",
  "temp": 21.5,
  "state_code": "NSW",
  "weather": {
    "description": "Broken clouds"
  }
}
```

---

## Utility Classes

### FileUtils

**File:** `src/test/java/utils/FileUtils.java`

Handles all test data file I/O. Both methods use a shared constant `TEST_DATA_PATH = "src/test/resources/testdata/"`.

| Method                                          | Returns         | Used By |
|-------------------------------------------------|-----------------|---------|
| `getCityIdsFromJson(String fileName)`           | `String`        | AC3     |
| `getUsStateCityIdsFromCsv(String fileName, int limit)` | `List<String>` | AC4 |

**`getCityIdsFromJson`** — reads a JSON array of city objects and joins their `id` fields:
```java
public static String getCityIdsFromJson(String fileName) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    List<Map<String, String>> cities = mapper.readValue(
            new File(TEST_DATA_PATH + fileName), new TypeReference<>() {});
    return cities.stream().map(city -> city.get("id")).collect(Collectors.joining(","));
}
```

**`getUsStateCityIdsFromCsv`** — reads a CSV and collects one city ID per US state up to a specified limit:
```java
public static List<String> getUsStateCityIdsFromCsv(String fileName, int limit) throws Exception {
    Map<String, String> stateMap = new HashMap<>();
    try (CSVReader reader = new CSVReader(new FileReader(TEST_DATA_PATH + fileName))) {
        String[] line;
        reader.readNext(); // Skip header
        while ((line = reader.readNext()) != null) {
            if ("US".equalsIgnoreCase(line[3])) {
                stateMap.put(line[2], line[0]); // state_code -> city_id
            }
            if (stateMap.size() >= limit) break;
        }
    }
    return new ArrayList<>(stateMap.values());
}
```

---

### ReportUtils

**File:** `src/test/java/utils/ReportUtils.java`

Builds formatted report strings that are logged to Serenity via `Serenity.recordReportData()`. Keeps all string formatting out of step definitions.

| Method                                                    | Used By | Serenity Report Title                        |
|-----------------------------------------------------------|---------|----------------------------------------------|
| `generateWeatherTable(List<CityWeather>)`                 | AC1     | "Full Weather Table"                         |
| `buildCoordinateReport(CityWeather)`                      | AC2     | "Coordinate Search Result"                   |
| `buildWarmestCityReport(CityWeather)`                     | AC3     | "Australian Capitals Temperature Analysis"   |
| `buildColdestStateReport(CityWeather, String stateCode)`  | AC4     | "Logistics Intelligence: Coldest US State"   |

**Example — `generateWeatherTable`:**
```java
public static String generateWeatherTable(List<CityWeather> weatherList) {
    StringBuilder report = new StringBuilder();
    report.append(String.format("%-20s | %-10s | %-15s\n", "City", "Temp", "Description"));
    report.append("------------------------------------------------------------\n");
    for (CityWeather city : weatherList) {
        report.append(String.format("%-20s | %-10.1f | %-15s\n",
                city.getCityName(), city.getTemp(), city.getWeather().getDescription()));
    }
    return report.toString();
}
```

---

## BDD Feature File

**File:** `src/test/resources/features/weather/weather.api.feature`

```gherkin
@api
Feature: Current Weather Data

  @AC1 @cityids
  Scenario: AC1_Retrieve current weather for multiple international cities
    Given I have a list of major international City IDs:
      | city_id | city_name     |
      | 2147714 | Sydney        |
      | 5128581 | New York City |
      | 2643743 | London        |
      | 1850147 | Tokyo         |
    When I request the current weather from Weatherbit
    Then the response should contain weather data for each city

  @AC2 @coordinates
  Scenario: AC2_Retrieve current weather using latitude and longitude
    When I fetch weather data for coordinates Latitude -33.865143 and Longitude 151.209900
    Then the response should contain valid data for these coordinates

  @AC3 @warmest
  Scenario: AC3_Identify the warmest Australian capital city from local data
    When I fetch current weather for all cities in "ausCapCities.json"
    Then I should identify and report the warmest city among them

  @AC4 @coldest
  Scenario: AC4_Identify the coldest US State from metadata
    When I identify the coldest US state using the metadata in "us_cities.csv"
    Then the system should report the state with the lowest current temperature
```

---

## Step Definitions

**File:** `src/test/java/api/steps/ApiStepDefinition.java`

Step definitions are kept thin — each method delegates to `WeatherApi`, `FileUtils`, or `ReportUtils`. No file I/O or string formatting lives in this class.

| Gherkin Step | Method | Delegates To |
|---|---|---|
| `I have a list of major international City IDs:` | `i_have_a_list_of_city_ids` | — (DataTable parsing) |
| `I request the current weather from Weatherbit` | `i_request_current_weather` | `WeatherApi.getWeatherByCityIds()` |
| `the response should contain weather data for each city` | `verify_response` | `ReportUtils.generateWeatherTable()` |
| `I fetch weather data for coordinates Latitude {double} and Longitude {double}` | `fetchWeatherByCoordinates` | `WeatherApi.getWeatherByCoords()` |
| `the response should contain valid data for these coordinates` | `verifyCoordinateResponse` | `ReportUtils.buildCoordinateReport()` |
| `I fetch current weather for all cities in {string}` | `fetchWeatherFromJsonFile` | `FileUtils.getCityIdsFromJson()` |
| `I should identify and report the warmest city among them` | `identifyWarmestCity` | `ReportUtils.buildWarmestCityReport()` |
| `I identify the coldest US state using the metadata in {string}` | `identifyColdestFromCSV` | `FileUtils.getUsStateCityIdsFromCsv()` |
| `the system should report the state with the lowest current temperature` | `reportColdestState` | `ReportUtils.buildColdestStateReport()` |

---

## Test Runner

**File:** `src/test/java/api/runner/ApiCucumberTestSuite.java`

```java
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features.weather")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty,timeline:build/test-results/timeline")
public class ApiCucumberTestSuite {}
```

To run a specific scenario from the runner, add `@IncludeTags`:

```java
@IncludeTags("AC1")   // runs only AC1
@IncludeTags("AC4")   // runs only AC4
// Remove @IncludeTags entirely to run all @api scenarios
```

---

## Test Data

| File | Location | Used By | Purpose |
|---|---|---|---|
| `ausCapCities.json` | `src/test/resources/testdata/` | AC3 | JSON array of Australian capital city objects with `id` fields |
| `us_cities.csv`     | `src/test/resources/testdata/` | AC4 | CSV with city ID (col 0), city name (col 1), state code (col 2), country code (col 3) |

**`ausCapCities.json` format:**
```json
[
  { "id": "2147714", "name": "Sydney" },
  { "id": "2063523", "name": "Perth" }
]
```

**`us_cities.csv` format:**
```
city_id,city_name,state_code,country_code
4887398,Chicago,IL,US
5128581,New York City,NY,US
```

---

## Cucumber Tags Reference

| Tag           | Scenario                                              |
|---------------|-------------------------------------------------------|
| `@api`        | All API scenarios (feature-level tag)                 |
| `@AC1`        | Weather by multiple city IDs (DataTable)              |
| `@AC2`        | Weather by latitude and longitude coordinates         |
| `@AC3`        | Warmest Australian capital from JSON file             |
| `@AC4`        | Coldest US state from CSV metadata file               |
| `@cityids`    | Alias for AC1                                         |
| `@coordinates`| Alias for AC2                                         |
| `@warmest`    | Alias for AC3                                         |
| `@coldest`    | Alias for AC4                                         |

---

## Executing Tests

### Prerequisites

- Java 17+
- Maven 3.6+

Verify:
```bash
java -version
mvn -version
```

### Run all API tests

```bash
mvn clean verify -Dcucumber.filter.tags="@api"
```

### Run a specific scenario by tag

```bash
# AC1 — Weather by city IDs
mvn clean verify -Dcucumber.filter.tags="@AC1"

# AC2 — Weather by coordinates
mvn clean verify -Dcucumber.filter.tags="@AC2"

# AC3 — Warmest Australian capital
mvn clean verify -Dcucumber.filter.tags="@AC3"

# AC4 — Coldest US state
mvn clean verify -Dcucumber.filter.tags="@AC4"
```

### Run multiple tags

```bash
mvn clean verify -Dcucumber.filter.tags="@AC1 or @AC2"
```

### Using the batch file (Windows)

A `run-api-tests.bat` file is included at the project root for one-click execution:

```bash
run-api-tests.bat
```

This runs all API tests tagged `@api` and opens the Serenity report automatically after the run.

### Generate the Serenity report separately

```bash
mvn serenity:aggregate
```

---

## Reports

After `mvn clean verify`, Serenity generates a rich HTML report at:

```
target/site/serenity/index.html
```

**Report includes:**
- Test outcome summary per feature and scenario
- Step-level breakdown with full request and response detail for every API call
- Custom data panels logged via `Serenity.recordReportData()` (weather tables, warmest/coldest city summaries)
- Living documentation view of all Gherkin scenarios

---

## Best Practices

- **Use `SerenityRest` for all API calls** — not raw RestAssured — so every request and response is captured automatically in the Serenity report.
- **Keep base URLs and API keys in `serenity.conf`** — never hard-code them in action classes or step definitions.
- **Deserialize responses into POJOs** — using `response.jsonPath().getList("data", CityWeather.class)` produces type-safe objects and clearer assertion messages.
- **Annotate POJOs with `@JsonIgnoreProperties(ignoreUnknown = true)`** — the Weatherbit response has many fields; this prevents deserialization failures from unused fields.
- **Keep step definitions thin** — all file I/O belongs in `FileUtils`, all report formatting belongs in `ReportUtils`, and all HTTP calls belong in `WeatherApi`. Step definitions only orchestrate.
- **Use `FileUtils` for all test data loading** — centralising file reads means path changes only need updating in one place.
- **Use `ReportUtils` for all report strings** — keeps `Serenity.recordReportData()` calls in step definitions to a single line and makes report formats reusable.
- **Tag every scenario** — use `@api`, `@AC1`–`@AC4`, and alias tags to enable granular execution from Maven or the runner.
- **Always run with `mvn clean`** — prevents stale Serenity output from previous runs polluting the aggregate report.
