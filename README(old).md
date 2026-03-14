![Last Commit](https://img.shields.io/github/last-commit/serenity-bdd/serenity-cucumber-starter) ![Java](https://img.shields.io/badge/Java-17-blue) ![Serenity](https://img.shields.io/badge/Serenity_BDD-5.0.4-brightgreen) ![Cucumber](https://img.shields.io/badge/Cucumber-7.33.0-green)

---

<div align="center">

# 🚀 Elevate Your Test Automation: Behaviour-Driven, Living Documentation, Ready to Scale.

## Serenity BDD + Cucumber + Selenium + Java: "Your One-Stop Solution for Web UI and REST API Test Automation"

</div>

---

Welcome to the **Serenity BDD Cucumber Framework**. This hybrid test automation framework combines the power of [Serenity BDD](https://serenity-bdd.info/), [Cucumber](https://cucumber.io/), [Selenium WebDriver](https://www.selenium.dev/), and [RestAssured](https://rest-assured.io/) to deliver a clean, readable, and maintainable solution for both UI and API testing in Java.

Written in **Gherkin**, tests are human-readable and serve as living documentation that is always in sync with your actual test results. The framework is ideal for QA engineers, developers, and business analysts who want to collaborate on automated acceptance testing with minimal friction.

## Key Features

- **Hybrid UI & API Testing**: A single framework covers both browser automation via Selenium WebDriver and REST API validation via RestAssured — no need to maintain separate projects.

- **Behaviour-Driven Development (BDD)**: Tests are written in plain English using Gherkin `.feature` files, making them accessible to non-technical stakeholders and serving as always up-to-date living documentation.

- **Page Object Model**: UI tests are structured using Serenity's `PageObject` base class, providing clean separation between test logic and page interactions, making tests easier to maintain and extend.

- **Shared Utility Interface**: A common `HelperMethods` interface provides reusable WebDriver utilities — explicit waits, scroll-into-view, page-load checks — shared across all page objects without duplication.

- **Serenity BDD Reporting**: After each run, Serenity generates a rich single-page HTML report with full step-level detail, screenshots on failure, and a timeline view — giving you instant visibility into what passed, failed, and why.

- **Environment-Aware Configuration**: Base URLs and environment properties are managed centrally in `serenity.conf`, supporting seamless switching between environments without touching test code.

- **Configurable Timeouts & Screenshots**: Timeout values and screenshot behaviour are driven by `serenity.properties`, keeping configuration out of your test code.

## Table of Contents

- [**Getting Started**](#getting-started)
  - [Tools & Frameworks](#tools--frameworks)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [**Project Structure**](#project-structure)
- [**Framework Design**](#framework-design)
  - [Page Objects](#page-objects)
  - [API Step Library](#api-step-library)
  - [Helper Methods](#helper-methods)
  - [Step Definitions](#step-definitions)
- [**Writing Tests**](#writing-tests)
  - [Writing a Feature File](#writing-a-feature-file)
  - [Writing Step Definitions](#writing-step-definitions)
- [**Configuration**](#configuration)
- [**Executing Tests**](#executing-tests)
  - [Running via Maven](#running-via-maven)
  - [Running via IDE](#running-via-ide)
  - [Filtering by Tags](#filtering-by-tags)
- [**Reports**](#reports)
- [**Best Practices**](#best-practices)

---

## Getting Started

### Tools & Frameworks

- **[Java 17](https://openjdk.org/projects/jdk/17/)**: The programming language powering the framework. Java 17 LTS provides modern language features and long-term support.
- **[Serenity BDD 5.0.4](https://serenity-bdd.info/)**: The core test automation and reporting framework. Provides deep Cucumber integration, WebDriver management, RestAssured support, and living documentation generation.
- **[Cucumber 7.33.0](https://cucumber.io/)**: BDD test runner that parses `.feature` files written in Gherkin and maps steps to Java methods.
- **[JUnit Platform 6.0.1](https://junit.org/junit5/)**: The test execution engine that launches Cucumber via the JUnit Platform Suite.
- **[Selenium WebDriver](https://www.selenium.dev/)**: Browser automation library used for UI testing, managed automatically by Serenity's WebDriver integration.
- **[RestAssured](https://rest-assured.io/)**: Java DSL for REST API testing, used through Serenity's `SerenityRest` wrapper for built-in step reporting.
- **[AssertJ 3.23.1](https://assertj.github.io/doc/)**: Fluent assertion library used in page objects for expressive and readable assertions.
- **[Logback 1.2.10](https://logback.qos.ch/)**: Logging framework configured via `logback-test.xml` for structured console output during test execution.
- **[Maven](https://maven.apache.org/)**: Build and dependency management tool. The `pom.xml` controls all dependencies, plugins, and report generation lifecycle.

### Prerequisites

Before you begin, ensure the following are installed on your machine:

- **Java 17+** — [Download JDK](https://adoptium.net/)
- **Maven 3.8+** — [Download Maven](https://maven.apache.org/download.cgi)
- **Google Chrome** (latest stable) — Serenity manages the ChromeDriver binary automatically
- **Git** — for cloning the repository

Verify your setup:

```bash
java -version
mvn -version
```

### Installation

1. Clone the repository:

```bash
git clone https://github.com/priyankamalviya2912-ui/CucumberBDDSerenityJavaSeleniumUIAndApi.git
cd CucumberBDDSerenityJavaSeleniumUIAndApi
```

2. Install dependencies:

```bash
mvn clean install -DskipTests
```

3. Run the tests to verify the setup:

```bash
mvn clean verify
```

---

## Project Structure

```
CucumberBDDSerenityJavaSeleniumUIAndApi/
├── src/
│   └── test/
│       ├── java/
│       │   ├── api/
│       │   │   └── AuthorApi.java                    # REST API step library
│       │   ├── pages/
│       │   │   ├── StampDutyLandingPage.java         # Landing page object
│       │   │   ├── MotorVehicleRegistrationPage.java # Calculator form page object
│       │   │   └── CalculatorPopupPage.java          # Result popup page object
│       │   ├── runner/
│       │   │   └── CucumberTestSuite.java            # JUnit Platform test runner
│       │   ├── stepdefinitions/
│       │   │   └── StepDefinitions.java              # Cucumber glue code (UI + API)
│       │   └── utility/
│       │       └── HelperMethods.java                # Shared WebDriver utility interface
│       └── resources/
│           ├── features/
│           │   └── nsw/
│           │       ├── stamp_duty_check.feature      # UI: NSW Stamp Duty Calculator
│           │       └── Author.feature                # API: Open Library Author details
│           ├── serenity.conf                         # Serenity & WebDriver configuration
│           ├── logback-test.xml                      # Logging configuration
│           └── junit-platform.properties             # Parallel execution settings
├── pom.xml                                           # Maven build configuration
└── serenity.properties                               # Project name & timeout settings
```

---

## Framework Design

### Page Objects

Page objects extend Serenity's `PageObject` base class and also implement the `HelperMethods` utility interface. Each page object is responsible for locating elements on a specific page and exposing methods that represent meaningful user interactions with that page.

Here is an example from `MotorVehicleRegistrationPage.java`:

```java
public class MotorVehicleRegistrationPage extends PageObject implements HelperMethods {

    private By registrationRadio = By.xpath("//label[contains(text(),'Yes')]");
    private By purchasePriceText = By.id("purchasePrice");
    private By calculateButton   = By.cssSelector("button[type='submit']");
    private String pageTitle     = "Motor vehicle registration duty calculator";

    public void verifyMotorRegistrationPageIsDisplayed() {
        waitForTitle(getDriver(), pageTitle);
    }

    public void selectYesOnRegistrationRadio() {
        waitForElementClickable(getDriver(), registrationRadio);
        scrollIntoView(getDriver(), registrationRadio);
        find(registrationRadio).click();
    }

    public void inputPurchasePriceText(String price) {
        waitForElementVisible(getDriver(), purchasePriceText);
        find(purchasePriceText).sendKeys(price);
    }

    public void clickCalculateButton() {
        waitForElementClickable(getDriver(), calculateButton);
        find(calculateButton).click();
    }
}
```

In this example, `MotorVehicleRegistrationPage` encapsulates all interactions with the motor vehicle registration calculator form. Locators are kept private and page interactions are exposed as clearly named methods, keeping step definitions clean and readable.

Page objects in this framework are found in the `pages` package:

| Page Object | Responsibility |
|---|---|
| `StampDutyLandingPage` | Navigates to the landing page and clicks "Check online" |
| `MotorVehicleRegistrationPage` | Handles radio buttons, price input, and calculate button |
| `CalculatorPopupPage` | Validates the duty result modal popup |

---

### API Step Library

REST API interactions are encapsulated in the `api` package. Classes extend `PageObject` and use Serenity's `SerenityRest` wrapper, so all API calls are automatically captured in the Serenity report with full request/response detail.

Here is an example from `AuthorApi.java`:

```java
public class AuthorApi extends PageObject {

    private String baseUrl;

    public void setBaseUrl() {
        EnvironmentVariables vars = SystemEnvironmentVariables.createEnvironmentVariables();
        baseUrl = vars.getProperty("restapi.baseurl", "https://openlibrary.org");
    }

    public void fetchAuthorDetails(String authorId) {
        setBaseUrl();
        SerenityRest.given()
            .baseUri(baseUrl)
            .when()
            .get("/authors/" + authorId + ".json")
            .then()
            .statusCode(200);
    }

    public void verifyPersonalName(String expectedName) {
        SerenityRest.lastResponse()
            .then()
            .body("personal_name", is(expectedName));
    }

    public void verifyAlternateNamesInclude(String expectedAltName) {
        SerenityRest.lastResponse()
            .then()
            .body("alternate_names", hasItem(expectedAltName));
    }
}
```

The base URL is read from the environment configuration in `serenity.conf`, allowing the API endpoint to be changed per environment without modifying test code.

---

### Helper Methods

`HelperMethods` is a Java interface with default methods providing shared WebDriver utilities across all page objects. Any page object that implements this interface gains access to the following methods:

| Method | Description |
|---|---|
| `getSerenityTimeout()` | Reads the `webdriver.wait.for.timeout` value from Serenity properties (default: 5000ms) |
| `scrollIntoView(driver, by)` | Uses JavaScript smooth scroll to bring an element into view before interacting |
| `waitForPageLoad(driver)` | Waits until `document.readyState` equals `"complete"` |
| `waitForElementVisible(driver, by)` | Explicit wait until the element is visible on screen |
| `waitForElementClickable(driver, by)` | Explicit wait until the element is ready to receive a click |
| `waitForTitle(driver, title)` | Waits until the browser page title matches the expected value |

All waits use `WebDriverWait` with `ExpectedConditions` and respect the configurable timeout from `serenity.properties`.

---

### Step Definitions

`StepDefinitions.java` is the single glue class mapping all Gherkin steps to page object and API actions. UI page objects are instantiated directly, while the API step library is injected via Serenity's `@Steps` annotation:

```java
@Steps
AuthorApi authorApi;

StampDutyLandingPage landingPage            = new StampDutyLandingPage();
MotorVehicleRegistrationPage calculatorPage = new MotorVehicleRegistrationPage();
CalculatorPopupPage popupPage               = new CalculatorPopupPage();
```

This keeps step definitions thin and focused on orchestration, with all interaction logic living in the page objects.

---

## Writing Tests

### Writing a Feature File

Feature files are written in Gherkin and located in `src/test/resources/features/nsw/`. Each scenario is tagged to allow selective execution via the test runner or Maven command line.

Here is an example of the UI feature:

```gherkin
@TC_01
Feature: Stamp Duty Calculator

  As a user
  I want to check stamp duty online
  So that I can calculate the expected duty amount

  Scenario Outline: Navigate to Stamp Duty calculator from landing page
    Given Open the Stamp Duty landing page
    When Click the 'Check online' button to navigate to the calculator
    Then The calculator page should be displayed
    And I select "<PassengerVehicle>" and "<PurchasePrice>" for Is this registration for a passenger vehicle
    And I verify the "Passenger vehicle" is "<PassengerVehicle>"
    And I verify the "Purchase price" is "<PurchasePrice>"
    And I verify the "Calculated duty" is "<CalculatedDuty>"

    Examples:
      | PassengerVehicle | PurchasePrice | CalculatedDuty |
      | Yes              | 45,000        | $1,350.00      |
```

Here is an example of the API feature:

```gherkin
@API @Task3 @TC_03
Feature: Validate Author API

  Scenario: Validate specific author details from Open Library
    When I request details for author with ID "OL1A"
    Then the personal name should be "Sachi Rautroy"
    And the alternate names should include "Yugashrashta Sachi Routray"
```

### Writing Step Definitions

Each Gherkin step maps to a method in `StepDefinitions.java`. Step methods delegate directly to page object methods, keeping them concise:

```java
@Given("Open the Stamp Duty landing page")
public void openLandingPage() {
    landingPage.open();
}

@When("Click the 'Check online' button to navigate to the calculator")
public void clickCheckOnline() {
    landingPage.clickCheckOnlineButton();
}

@Then("The calculator page should be displayed")
public void verifyCalculatorPageDisplayed() {
    calculatorPage.verifyMotorRegistrationPageIsDisplayed();
}

@When("I request details for author with ID {string}")
public void requestAuthorDetails(String authorId) {
    authorApi.fetchAuthorDetails(authorId);
}
```

---

## Configuration

### `serenity.conf`

The main configuration file located at `src/test/resources/serenity.conf`. Controls WebDriver, browser options, screenshots, and environment-specific URLs:

```hocon
serenity {
  take.screenshots = FOR_FAILURES
  accessibility.reporting = true
  full.logging = true
}

headless.mode = false
webdriver.driver = chrome

chrome.capabilities {
  browserName = "chrome"
  acceptInsecureCerts = true
  "goog:chromeOptions" {
    args = ["--no-sandbox", "--window-size=1000,800", "--incognito",
            "--disable-gpu", "--disable-dev-shm-usage"]
  }
}

environments {
  default {
    webdriver.base.url = "https://www.service.nsw.gov.au/transaction/check-motor-vehicle-stamp-duty"
    restapi.baseurl    = "https://openlibrary.org"
  }
}
```

### `serenity.properties`

Located at the project root. Sets the project display name, global timeout, and screenshot behaviour:

```properties
serenity.project.name=Serenity and Cucumber Quick Start
serenity.timeout=5000
serenity.take.screenshots=FOR_FAILURES
```



---

## Executing Tests

### Running via Maven

Run the full test suite:

```bash
mvn clean verify
```

Run and generate Serenity reports in a single command:

```bash
mvn clean verify serenity:aggregate
```

### Filtering by Tags

Run only UI tests:

```bash
mvn clean verify -Dcucumber.filter.tags="@TC_01"
```

Run only API tests:

```bash
mvn clean verify -Dcucumber.filter.tags="@TC_03"
```

Run multiple tags:

```bash
mvn clean verify -Dcucumber.filter.tags="@TC_01 or @TC_03"
```

### Switching Environments

Pass the `environment` system property to switch the base URLs defined in `serenity.conf`:

```bash
mvn clean verify -Denvironment=staging
```

### Switching Browsers

Override the browser at runtime:

```bash
mvn clean verify -Ddriver=firefox
```

### Running in Headless Mode

```bash
mvn clean verify -Dheadless=true
```

### Running via IDE

Run the `CucumberTestSuite` class directly from IntelliJ IDEA or Eclipse. It is pre-configured to pick up all scenarios tagged `@TC_01` from the `features/nsw` directory.

To run API tests from the IDE, update the `@IncludeTags` annotation in `CucumberTestSuite.java`:

```java
@IncludeTags("TC_03")   // for API tests
// or remove the annotation entirely to run all scenarios
```

### Parallel Execution

Parallel execution is enabled by default via `junit-platform.properties` with 4 fixed threads. To adjust the thread count, update the following properties:

```properties
cucumber.execution.parallel.config.fixed.parallelism=4
cucumber.execution.parallel.config.fixed.max-pool-size=4
```

---

## Reports

Serenity generates rich, interactive HTML reports after each test run.

### View the report

Open the following file in your browser after running `mvn clean verify`:

```
target/site/serenity/index.html
```

### Generate reports manually

If you run tests without the full Maven lifecycle, generate reports separately:

```bash
mvn serenity:aggregate
```

### What the report includes

- **Test outcome summary** — pass/fail counts per feature and scenario
- **Step-level breakdown** — every Gherkin step with its result and duration
- **Screenshots** — captured automatically on test failure
- **Living documentation** — feature narratives and scenario descriptions rendered as readable documentation

---

## Best Practices

- **Use `HelperMethods` for all waits**: Avoid `Thread.sleep()` entirely. Use `waitForElementVisible`, `waitForElementClickable`, and `waitForPageLoad` from `HelperMethods` so waits are consistent, configurable, and logged.

- **Keep step definitions thin**: Step definition methods should do nothing more than call a single page object method. Business logic and element interactions belong in page objects, not in step definitions.

- **One page object per page**: Each distinct page or component should have its own page object class. Do not mix locators from different pages into a single class.

- **Configure base URLs in `serenity.conf`**: Never hard-code URLs in page objects or step definitions. Use the environment configuration in `serenity.conf` so that switching environments requires no code change.

- **Tag scenarios meaningfully**: Use tags like `@TC_01`, `@API`, `@smoke`, `@regression` to enable targeted execution. Every scenario should have at least one tag.

- **Use `SerenityRest` for all API calls**: Always use `SerenityRest` instead of raw `RestAssured` so that API requests and responses are automatically included in the Serenity report.

- **Assert in page objects, not step definitions**: Assertions belong in page object methods (using AssertJ or Hamcrest), keeping step definitions free of assertion logic and making assertion behaviour reusable.

- **Use Scenario Outline for data-driven tests**: When a scenario needs to run with multiple data sets, use `Scenario Outline` with an `Examples` table rather than duplicating scenarios.

- **Run with `mvn clean verify`**: Always use `clean` to avoid stale test results polluting Serenity's aggregate report from a previous run.

---


