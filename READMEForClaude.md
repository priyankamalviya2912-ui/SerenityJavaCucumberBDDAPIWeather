![Last Commit](https://img.shields.io/github/last-commit/serenity-bdd/serenity-cucumber-starter) ![Java](https://img.shields.io/badge/Java-17-blue) ![Serenity](https://img.shields.io/badge/Serenity_BDD-5.0.4-brightgreen) ![Cucumber](https://img.shields.io/badge/Cucumber-7.33.0-green)

---

<div align="center">

# Serenity BDD + Cucumber + Selenium + RestAssured

## Hybrid UI and REST API Test Automation Framework

</div>

---

Welcome to the **Serenity BDD Cucumber Framework**. This hybrid automation framework combines [Serenity BDD](https://serenity-bdd.info/), [Cucumber](https://cucumber.io/), [Selenium WebDriver](https://www.selenium.dev/), and [RestAssured](https://rest-assured.io/) to deliver clean, readable, and maintainable UI and API tests in Java.

Tests are written in **Gherkin** and serve as living documentation that stays in sync with actual test results.

---

## Key Features

- **Hybrid UI & API Testing** — one framework for browser automation (Selenium) and REST API validation (RestAssured).
- **BDD with Cucumber** — tests written in plain Gherkin, readable by non-technical stakeholders.
- **Page Object Model** — UI page interactions are encapsulated in Serenity `PageObject` classes.
- **POJO Deserialization** — API responses are deserialized into typed Java POJOs using Jackson.
- **POST Body via Map or POJO** — POST request bodies can be sent using a `HashMap` or a Java POJO, both loaded from JSON files.
- **JSON-driven Test Data** — test inputs and POST payloads are managed in external JSON files read via `JsonPath` or `JsonReader`.
- **Authentication Examples** — dedicated reference classes demonstrating Basic Auth, Bearer Token, API Key (query param and header), and OAuth2.
- **Environment-aware Config** — all base URLs live in `serenity.conf`, never in code.
- **Serenity BDD Reporting** — rich single-page HTML report with step-level detail and failure screenshots.

---

## Table of Contents

- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [API Configuration](#api-configuration-serenityconf)
- [APIs Under Test](#apis-under-test)
  - [Open Library — GET with JSON File + POJO Validation](#1-open-library-api--get-with-json-file--pojo-validation)
  - [SpaceX Latest Launch — GET with POJO](#2-spacex-latest-launch-api--get-with-pojo)
  - [Dummy REST API — Employee POST (Map and POJO)](#3-dummy-rest-api--employee-post-map-and-pojo)
- [Authentication Types](#authentication-types)
  - [Basic Auth](#1-basic-auth)
  - [Bearer Token](#2-bearer-token)
  - [API Key as Query Param](#3-api-key-as-query-parameter)
  - [API Key as Header](#4-api-key-as-header)
  - [OAuth2 (Login + Token)](#5-oauth2-login-then-use-token)
- [BDD Feature File](#bdd-feature-file)
- [Step Definitions](#step-definitions)
- [Package Structure Detail](#package-structure-detail)
- [Test Runners](#test-runners)
- [Test Data](#test-data)
- [Executing Tests](#executing-tests)
- [Reports](#reports)
- [Best Practices](#best-practices)

---

## Technology Stack

| Tool / Library         | Version        | Purpose                                    |
|------------------------|----------------|--------------------------------------------|
| Java                   | 17             | Programming language                       |
| Maven                  | 3.x            | Build and dependency management            |
| Serenity BDD           | 5.0.4          | Test orchestration and HTML reporting      |
| Cucumber               | 7.33.0         | BDD feature file parsing and execution     |
| Serenity REST Assured  | 5.0.4          | RestAssured wrapper with Serenity logging  |
| Selenium WebDriver     | via Serenity   | Browser UI automation                      |
| JUnit Platform         | 6.0.1          | Test runner platform (Suite API)           |
| Jackson Databind       | 2.17.0         | JSON serialization and POJO deserialization|
| AssertJ                | 3.23.1         | Fluent assertion library                   |
| Logback                | 1.2.10         | Logging                                    |

---

## Project Structure

```
CucumberBDDSerenityJavaSeleniumUIAndApi-main/
├── pom.xml
├── serenity.properties
└── src/
    └── test/
        ├── java/
        │   ├── api/                                  # API action classes (RestAssured calls)
        │   │   ├── AuthorApi.java                    # GET - Open Library authors endpoint
        │   │   ├── SpacexApi.java                    # GET - SpaceX latest launch endpoint
        │   │   ├── EmployeeApi.java                  # POST - Employee creation (Map + POJO)
        │   │   ├── authTyp/                          # Authentication reference examples
        │   │   │   ├── BasicAuthExample.java         # HTTP Basic Auth (.auth().basic())
        │   │   │   ├── BearerTokenExample.java       # Bearer token via Authorization header
        │   │   │   ├── ApiKeyExample.java            # API Key as query parameter
        │   │   │   ├── ApiKeyHeaderExample.java      # API Key as request header
        │   │   │   └── OAuth2Example.java            # OAuth2: login to get token, then use it
        │   │   ├── steps/
        │   │   │   └── ApiStepDefinition.java        # Cucumber step definitions for all API scenarios
        │   │   ├── runner/
        │   │   │   └── ApiCucumberTestSuite.java     # API test runner
        │   │   └── pojo/
        │   │       ├── Author.java                   # POJO - Open Library author response
        │   │       ├── Type.java                     # POJO - Author type sub-object
        │   │       ├── Employee.java                 # POJO - Employee POST request/response
        │   │       └── spacexpojo/
        │   │           ├── Spacex.java               # POJO - SpaceX launch response
        │   │           ├── Core.java                 # POJO - SpaceX core data
        │   │           └── Links.java                # POJO - SpaceX launch links
        │   ├── ui/                                   # UI automation layer
        │   │   ├── pages/
        │   │   │   ├── StampDutyLandingPage.java
        │   │   │   ├── MotorVehicleRegistrationPage.java
        │   │   │   └── CalculatorPopupPage.java
        │   │   ├── steps/
        │   │   │   └── StepDefinitions.java
        │   │   └── runner/
        │   │       └── CucumberTestSuite.java
        │   └── utils/
        │       ├── JsonReader.java                   # Reads test data and payload JSON files
        │       └── HelperMethods.java                # Shared WebDriver utility methods
        └── resources/
            ├── serenity.conf                         # Environment config and base URLs
            ├── junit-platform.properties             # Parallel execution settings
            ├── logback-test.xml                      # Logging configuration
            ├── features/
            │   └── nsw/
            │       ├── api.author.feature            # API BDD scenarios (GET + POST)
            │       └── stamp_duty_check.feature      # UI BDD scenarios
            ├── testdata/
            │   ├── authorData.json                   # GET test data for Open Library
            │   └── spacex.json                       # SpaceX reference data
            └── payload/
                └── createEmployee.json               # POST request bodies (tc01, tc02)
```

---

## API Configuration (`serenity.conf`)

All base URLs are environment-driven and defined centrally in `src/test/resources/serenity.conf`. They are loaded at runtime via Serenity's `EnvironmentSpecificConfiguration` — no URLs are hard-coded in test code.

```hocon
environments {
    default {
        webdriver.base.url       = "https://www.service.nsw.gov.au/transaction/check-motor-vehicle-stamp-duty"
        restapi.baseurl          = "https://openlibrary.org"
        restapi.spacex.baseurl   = "https://api.spacexdata.com/v4/launches/latest"
        restapi.emp.baseurl      = "https://dummy.restapiexample.com/api/v1"
    }
}
```

| Config Key               | Base URL                                         | Used By         |
|--------------------------|--------------------------------------------------|-----------------|
| `webdriver.base.url`     | `https://www.service.nsw.gov.au/...`             | UI tests        |
| `restapi.baseurl`        | `https://openlibrary.org`                        | AuthorApi       |
| `restapi.spacex.baseurl` | `https://api.spacexdata.com/v4/launches/latest`  | SpacexApi       |
| `restapi.emp.baseurl`    | `https://dummy.restapiexample.com/api/v1`        | EmployeeApi     |

---

## APIs Under Test

### 1. Open Library API — GET with JSON File + POJO Validation

| Property     | Value                                    |
|--------------|------------------------------------------|
| Base URL     | `https://openlibrary.org`                |
| Endpoint     | `/authors/{authorId}.json`               |
| HTTP Method  | `GET`                                    |
| Config Key   | `restapi.baseurl`                        |
| Cucumber Tag | `@TC_03`                                 |
| Action Class | `api.AuthorApi`                          |

**What it does:**

1. Reads the `authorId` from `testdata/authorData.json` using RestAssured's `JsonPath.from(file)`.
2. Sends a `GET` request to `/authors/{authorId}.json`.
3. Validates `personal_name` using two approaches:
   - **Direct JsonPath assertion** — `.body("personal_name", equalTo(expectedName))`
   - **POJO deserialization** — deserializes the response into `Author` using `response.as(Author.class)`, then uses Hamcrest to assert `author.getName()`.
4. Validates `alternate_names` list contains the expected alternate name with `hasItem(...)`.

**RestAssured GET call:**
```java
// Read authorId from JSON file
File file = new File("src/test/resources/testdata/author.json");
JsonPath jsonPath = JsonPath.from(file);
String authorId = jsonPath.getString("author.authorId");

response = SerenityRest
        .given()
        .baseUri(baseUrl)
        .when()
        .get("/authors/" + authorId + ".json");
```

**POJO validation (response deserialization):**
```java
// Deserialize full response into Author POJO
Author author = response.as(Author.class);

// Read expected value from JSON file
String expectedName = JsonPath.from(file).getString("author.personalName");

assertThat(author.getName(), equalTo(expectedName));

// Print fields from POJO
System.out.println("Author Name: " + author.getName());
System.out.println("Birth Date: " + author.getBirth_date());
System.out.println("Type Key:   " + author.getType().getKey());
```

**Direct body assertion (alternative):**
```java
response.then()
        .statusCode(200)
        .body("personal_name", equalTo(expectedName));

response.then()
        .body("alternate_names", hasItem(expectedAltName));
```

**POJO classes:**

| Class         | Package    | Fields                                        |
|---------------|------------|-----------------------------------------------|
| `Author.java` | `api.pojo` | `name`, `personal_name`, `birth_date`, `type` |
| `Type.java`   | `api.pojo` | `key`                                         |

Both are annotated with `@JsonIgnoreProperties(ignoreUnknown = true)` so extra response fields are safely ignored.

**Test data:** `src/test/resources/testdata/authorData.json`
```json
{
  "author": {
    "authorId": "OL1A",
    "personalName": "Sachi Routray",
    "alternateName": "Yugashrashta Sachi Routray"
  }
}
```

---

### 2. SpaceX Latest Launch API — GET with POJO

| Property     | Value                                                   |
|--------------|---------------------------------------------------------|
| Base URL     | `https://api.spacexdata.com/v4/launches/latest`         |
| Endpoint     | *(base URL is the full endpoint)*                       |
| HTTP Method  | `GET`                                                   |
| Config Key   | `restapi.spacex.baseurl`                                |
| Cucumber Tag | `@TC_04`                                                |
| Action Class | `api.SpacexApi`                                         |

**What it does:**

1. Sends a `GET` to the SpaceX latest launch endpoint (no additional path).
2. Deserializes the full response into the `Spacex` POJO using `response.as(Spacex.class)`.
3. Asserts that the `cores` list is non-empty and that the first core has a non-null `core` ID and `flight` number.

**RestAssured GET call:**
```java
response = SerenityRest
        .given()
        .baseUri(baseUrl)
        .when()
        .get();
```

**POJO validation:**
```java
Spacex spacex = response.as(Spacex.class);

assertThat(spacex.getCores().size(), greaterThan(0));
assertThat(spacex.getCores().get(0).getCore(), notNullValue());
assertThat(spacex.getCores().get(0).getFlight(), notNullValue());
```

**POJO classes:**

| Class         | Package               | Fields mapped                                 |
|---------------|-----------------------|-----------------------------------------------|
| `Spacex.java` | `api.pojo.spacexpojo` | `links`, `rocket`, `success`, `crew`, `cores` |
| `Core.java`   | `api.pojo.spacexpojo` | `core`, `flight`, `reused`                    |
| `Links.java`  | `api.pojo.spacexpojo` | `webcast`, `article`, `wikipedia`             |

All three use `@JsonIgnoreProperties(ignoreUnknown = true)` to handle the large SpaceX response safely.

---

### 3. Dummy REST API — Employee POST (Map and POJO)

| Property     | Value                                           |
|--------------|-------------------------------------------------|
| Base URL     | `https://dummy.restapiexample.com/api/v1`       |
| Endpoint     | `/create`                                       |
| HTTP Method  | `POST`                                          |
| Config Key   | `restapi.emp.baseurl`                           |
| Cucumber Tag | `@TC_05`                                        |
| Action Class | `api.EmployeeApi`                               |

This API is called **twice** in the test, demonstrating two different ways to build the POST body.

---

#### Approach 1 — POST body from a HashMap (loaded from JSON)

Reads the JSON file using `JsonPath`, extracts the test case node (`tc01`) as a `Map<String, String>`, then passes the map directly as the body. RestAssured serializes the map to JSON automatically.

```java
File payload = new File("src/test/resources/payload/createEmployee.json");
JsonPath jsonPath = new JsonPath(payload);

Map<String, String> payloadAsHmap = new HashMap<>();
payloadAsHmap = jsonPath.getMap("tc01");     // extracts the "tc01" object as a Map

response = SerenityRest
        .given()
        .baseUri(baseUrl)
        .contentType("application/json")
        .body(payloadAsHmap)                 // Map is serialized to JSON body
        .when()
        .post("/create");
```

---

#### Approach 2 — POST body from a POJO (loaded from JSON)

Reads the JSON file using `JsonPath.from(payload).getObject("tc01", Employee.class)` — this maps the `tc01` JSON node directly into the `Employee` POJO. The POJO is then passed as the body. Jackson serializes it to JSON.

```java
File payload = new File("src/test/resources/payload/createEmployee.json");

Employee empPojo = JsonPath.from(payload)
        .getObject("tc01", Employee.class);  // deserialize JSON node into POJO

System.out.println(empPojo.getName());
System.out.println(empPojo.getSalary());
System.out.println(empPojo.getAge());

response = SerenityRest
        .given()
        .baseUri(baseUrl)
        .contentType("application/json")
        .body(empPojo)                       // POJO is serialized to JSON body
        .when()
        .post("/create");
```

---

#### Response Validation

After the POST, the response is validated by reading the expected values back from the same JSON payload file and comparing them with the actual response fields:

```java
// Read expected values from payload file
JsonPath jsonPathOfEmp = JsonPath.from(payload);
String name   = jsonPathOfEmp.getString("tc01.name");
String salary = jsonPathOfEmp.getString("tc01.salary");
String age    = jsonPathOfEmp.getString("tc01.age");

// Assert response
assertThat(response.getStatusCode(), equalTo(200));
assertThat(jsonPath.getString("status"),      equalTo("success"));
assertThat(jsonPath.getString("data.name"),   equalTo(name));
assertThat(jsonPath.getString("data.salary"), equalTo(salary));
assertThat(jsonPath.getString("data.age"),    equalTo(age));
```

**POST payload file:** `src/test/resources/payload/createEmployee.json`
```json
{
  "tc01": {
    "name": "Ron",
    "salary": "123",
    "age": "23"
  },
  "tc02": {
    "name": "Danny",
    "salary": "2499",
    "age": "28"
  }
}
```

Multiple test cases (`tc01`, `tc02`) are stored in one file. The test case ID (`tc01`, `tc02`) is used as the JSON path key to extract data for a specific run.

**Employee POJO:** `api.pojo.Employee`
```java
public class Employee {
    private String name;
    private String salary;
    private String age;
    // constructors, getters, setters
}
```

---

### JsonReader Utility

`utils.JsonReader` is a shared utility class that provides reusable methods for reading JSON test data files.

```java
// Load authorData.json at class load time (static block)
String authorId    = JsonReader.getValue("authorId");     // reads author.authorId
String authorName  = JsonReader.getValue("personalName"); // reads author.personalName

// Load employee payload JSON on demand
JsonNode empData = JsonReader.empData();

// Load any test case from employees.json as a HashMap
HashMap<String, Object> tc01 = JsonReader.getTestData("tc01");
```

| Method                   | Returns                  | Source File                           |
|--------------------------|--------------------------|---------------------------------------|
| `getValue(key)`          | `String`                 | `testdata/authorData.json`            |
| `empData()`              | `JsonNode`               | `payload/createEmployee.json`         |
| `getTestData(tcId)`      | `HashMap<String,Object>` | `testdata/employees.json`             |

---

## Authentication Types

The `api.authTyp` package contains **standalone reference classes** (each with a `main` method) demonstrating how to set up the five most common API authentication patterns using RestAssured. These are educational examples — they are not wired into the Cucumber BDD flow.

---

### 1. Basic Auth

**File:** `api/authTyp/BasicAuthExample.java`

Sends username and password via HTTP Basic Authentication. RestAssured encodes them as a Base64 `Authorization: Basic ...` header automatically.

```java
Response response = RestAssured
        .given()
        .auth()
        .basic("user", "passwd")
        .when()
        .get("https://httpbin.org/basic-auth/user/passwd");
```

- **When to use:** APIs that accept `Authorization: Basic base64(user:password)`.
- **RestAssured method:** `.auth().basic(username, password)`

---

### 2. Bearer Token

**File:** `api/authTyp/BearerTokenExample.java`

Sends a pre-obtained token in the `Authorization` header with the `Bearer` prefix.

```java
String token = "12345";

Response response = RestAssured
        .given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("https://httpbin.org/bearer");
```

- **When to use:** APIs that require a JWT or OAuth access token passed in the Authorization header.
- **RestAssured method:** `.header("Authorization", "Bearer " + token)` — set as a plain header.

---

### 3. API Key as Query Parameter

**File:** `api/authTyp/ApiKeyExample.java`

Passes the API key as a query parameter appended to the URL (e.g., `?appid=YOUR_KEY`).

```java
Response response = RestAssured
        .given()
        .queryParam("q", "London")
        .queryParam("appid", "YOUR_API_KEY")
        .when()
        .get("https://api.openweathermap.org/data/2.5/weather");
```

- **When to use:** APIs like OpenWeatherMap that accept `?apikey=xxx` in the URL.
- **RestAssured method:** `.queryParam(key, value)`

---

### 4. API Key as Header

**File:** `api/authTyp/ApiKeyHeaderExample.java`

Passes the API key in a custom request header (e.g., `x-api-key`).

```java
String apiKey = "YOUR_API_KEY";

Response response = RestAssured
        .given()
        .header("x-api-key", apiKey)
        .when()
        .get("https://api.thecatapi.com/v1/images/search");
```

- **When to use:** APIs that require a key in a custom header like `x-api-key`, `api-key`, or `Authorization`.
- **RestAssured method:** `.header(headerName, value)`

---

### 5. OAuth2 — Login then Use Token

**File:** `api/authTyp/OAuth2Example.java`

A two-step pattern: first POST to a login endpoint to retrieve a token, then use that token with RestAssured's built-in OAuth2 support for the actual API call.

```java
// Step 1: Login and retrieve the token
String body = "{ \"username\": \"mor_2314\", \"password\": \"83r5^_\" }";

Response loginResponse = RestAssured
        .given()
        .header("Content-Type", "application/json")
        .body(body)
        .post("https://fakestoreapi.com/auth/login");

String token = loginResponse.jsonPath().getString("token");
System.out.println("Token: " + token);

// Step 2: Use the token with OAuth2
Response response = RestAssured
        .given()
        .auth()
        .oauth2(token)
        .get("https://fakestoreapi.com/products");
```

- **When to use:** APIs that require a login call first, then authenticate subsequent calls with the returned JWT/OAuth token.
- **RestAssured method:** `.auth().oauth2(token)` — RestAssured adds `Authorization: Bearer <token>` automatically.
- **Key pattern:** Extract the token with `.jsonPath().getString("token")` and store it for reuse.

---

### Auth Type Summary

| Class                   | Auth Method              | RestAssured API                           |
|-------------------------|--------------------------|-------------------------------------------|
| `BasicAuthExample`      | HTTP Basic Auth          | `.auth().basic(user, password)`           |
| `BearerTokenExample`    | Bearer Token (header)    | `.header("Authorization", "Bearer " + t)`|
| `ApiKeyExample`         | API Key (query param)    | `.queryParam("appid", key)`               |
| `ApiKeyHeaderExample`   | API Key (header)         | `.header("x-api-key", key)`               |
| `OAuth2Example`         | OAuth2 (login + token)   | `.auth().oauth2(token)`                   |

---

## BDD Feature File

**File:** `src/test/resources/features/nsw/api.author.feature`

```gherkin
Feature: Open Library and validate details of Author - API Validation

  @API @Task3 @TC_03
  Scenario: Validate specific author details from Open Library using JSON test data
    When user sets the API base URL
    When I request details for author from json
    Then validate personal name from json
    And validate alternate name from json

  @API @Task3 @TC_04
  Scenario: Validate specific spacex
    When user sets the API base URL and verifies info for spacex api

  @API @Task3 @TC_05
  Scenario: Post Employee api
    When user sets the API base URL and post emp info
```

---

## Step Definitions

**File:** `src/test/java/api/steps/ApiStepDefinition.java`

Wires Gherkin steps to the API action classes. Serenity injects the action class instances via the `PageObject` base class.

| Gherkin Step                                                | Method(s) Called                                                                 | API              |
|-------------------------------------------------------------|----------------------------------------------------------------------------------|------------------|
| `user sets the API base URL`                                | `AuthorApi.setBaseUrl()`                                                         | Open Library     |
| `I request details for author from json`                    | `AuthorApi.getAuthorFromJson()`                                                  | Open Library GET |
| `validate personal name from json`                          | `AuthorApi.validatePersonalNameFromPojo()`                                       | Open Library GET |
| `validate alternate name from json`                         | `AuthorApi.validateAlternateName()`                                              | Open Library GET |
| `user sets the API base URL and verifies info for spacex api` | `SpacexApi.setBaseUrl()` → `getSpacexResponse()` → `validateCoreDataFromPojo()` | SpaceX GET       |
| `user sets the API base URL and post emp info`              | `EmployeeApi.setBaseUrl()` → `postNewEmployee()` → `postNewEmployeeUsingPojo()` → `validatePostNewEmployeeResponse()` | Employee POST |

---

## Package Structure Detail

```
src/test/java/
│
├── api/                          # REST API action layer
│   ├── AuthorApi.java            # Open Library GET: reads JSON file, GET, POJO + JsonPath assertions
│   ├── SpacexApi.java            # SpaceX GET: GET, POJO deserialization, Hamcrest assertions
│   ├── EmployeeApi.java          # Employee POST: Map body, POJO body, JsonPath response validation
│   │
│   ├── authTyp/                  # Authentication reference examples (standalone main classes)
│   │   ├── BasicAuthExample.java
│   │   ├── BearerTokenExample.java
│   │   ├── ApiKeyExample.java
│   │   ├── ApiKeyHeaderExample.java
│   │   └── OAuth2Example.java
│   │
│   ├── steps/
│   │   └── ApiStepDefinition.java   # All @When/@Then step bindings for API scenarios
│   │
│   ├── runner/
│   │   └── ApiCucumberTestSuite.java
│   │
│   └── pojo/
│       ├── Author.java              # Maps Open Library author response
│       ├── Type.java                # Maps author type sub-object
│       ├── Employee.java            # Maps employee request/response fields
│       └── spacexpojo/
│           ├── Spacex.java
│           ├── Core.java
│           └── Links.java
│
├── ui/                           # Selenium UI automation layer
│   ├── pages/
│   │   ├── StampDutyLandingPage.java
│   │   ├── MotorVehicleRegistrationPage.java
│   │   └── CalculatorPopupPage.java
│   ├── steps/
│   │   └── StepDefinitions.java
│   └── runner/
│       └── CucumberTestSuite.java
│
└── utils/
    ├── JsonReader.java            # Jackson + JsonPath utility: read authorData.json and employee payloads
    └── HelperMethods.java         # Shared WebDriver utilities (waits, scroll, page load)
```

---

## Test Runners

### API Test Runner

**File:** `src/test/java/api/runner/ApiCucumberTestSuite.java`

```java
@Suite
@IncludeEngines("cucumber")
@IncludeTags("TC_05")                        // Change to TC_03, TC_04, TC_05, or API as needed
@SelectPackages("features.nsw")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty,timeline:build/test-results/timeline")
public class ApiCucumberTestSuite {}
```

> Update `@IncludeTags` to run the desired scenario. Use `@API` to run all API scenarios.

### UI Test Runner

**File:** `src/test/java/ui/runner/CucumberTestSuite.java`

```java
@Suite
@IncludeEngines("cucumber")
@IncludeTags("TC_01")
@SelectClasspathResource("/features/nsw")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty,timeline:build/test-results/timeline")
public class CucumberTestSuite {}
```

---

## Test Data

| File                                             | Used By      | Purpose                                                         |
|--------------------------------------------------|--------------|-----------------------------------------------------------------|
| `src/test/resources/testdata/authorData.json`    | `AuthorApi`  | Author ID, expected personal name, alternate name for GET assertions |
| `src/test/resources/payload/createEmployee.json` | `EmployeeApi`| POST body and expected values for tc01 and tc02                |
| `src/test/resources/testdata/spacex.json`        | (reference)  | SpaceX reference data                                          |

**How data is loaded:**

```java
// JsonPath approach — used in AuthorApi and EmployeeApi
File file = new File("src/test/resources/testdata/authorData.json");
String authorId = JsonPath.from(file).getString("author.authorId");

// JsonPath.getMap — extract JSON object as a Map for POST body
Map<String, String> map = new JsonPath(payloadFile).getMap("tc01");

// JsonPath.getObject — deserialize JSON node into a POJO
Employee emp = JsonPath.from(payloadFile).getObject("tc01", Employee.class);

// JsonReader utility — Jackson ObjectMapper wrapper
String value = JsonReader.getValue("authorId");
JsonNode empData = JsonReader.empData();
HashMap<String, Object> tc = JsonReader.getTestData("tc01");
```

---

## Cucumber Tags Reference

| Tag      | Scenario                                      | Type |
|----------|-----------------------------------------------|------|
| `TC_01`  | NSW Stamp Duty UI calculator test             | UI   |
| `TC_03`  | Open Library — GET author details validation  | API  |
| `TC_04`  | SpaceX — GET latest launch validation         | API  |
| `TC_05`  | Employee — POST create new employee           | API  |
| `@API`   | All API scenarios                             | API  |
| `@Task3` | Group tag for all API task scenarios          | API  |

---

## Executing Tests

### Prerequisites

- Java 17+
- Maven 3.6+
- Google Chrome (latest stable)

Verify:
```bash
java -version
mvn -version
```

### Clone and install

```bash
git clone <repository-url>
cd CucumberBDDSerenityJavaSeleniumUIAndApi-main
mvn clean install -DskipTests
```

### Run all tests

```bash
mvn clean verify
```

### Run by tag

```bash
# Open Library GET test
mvn clean verify -Dtags="TC_03"

# SpaceX GET test
mvn clean verify -Dtags="TC_04"

# Employee POST test
mvn clean verify -Dtags="TC_05"

# All API tests
mvn clean verify -Dtags="@API"

# UI Stamp Duty test
mvn clean verify -Dtags="TC_01"
```

### Switching environments

```bash
mvn clean verify -Denvironment=staging
```

---

## Reports

After `mvn clean verify`, Serenity generates a rich HTML report:

```
target/site/serenity/index.html
```

To generate the report separately:

```bash
mvn serenity:aggregate
```

**Report includes:**
- Test outcome summary per feature and scenario
- Step-level breakdown with request/response detail for API calls
- Screenshots captured on test failure
- Living documentation view of Gherkin scenarios

---

## Best Practices

- **Use `SerenityRest` for all API calls** — not raw RestAssured — so every request and response is captured automatically in the Serenity report.
- **Keep base URLs in `serenity.conf`** — never hard-code URLs in action classes or step definitions.
- **Use POJOs for response assertions** — deserializing responses with Jackson POJOs produces clearer assertion messages and is more maintainable than raw JSON path strings.
- **Two ways to build a POST body** — use `HashMap` for simple key-value payloads; use a POJO when you need type safety and want Jackson to serialize the object to JSON.
- **Drive POST body and expected values from the same JSON file** — the payload file doubles as the source of truth for both the request body and the assertion values, so they never drift apart.
- **Keep step definitions thin** — step methods should call a single action class method. All request logic and assertions belong in the API action class.
- **Use `@JsonIgnoreProperties(ignoreUnknown = true)` on all POJOs** — real API responses usually have many more fields than you need; this annotation prevents deserialization failures from unknown fields.
- **Tag every scenario** — use tags like `@TC_03`, `@API`, `@smoke` to enable selective execution from the runner or Maven command line.
- **Always run with `mvn clean`** — prevents stale Serenity output from previous runs polluting the aggregate report.
