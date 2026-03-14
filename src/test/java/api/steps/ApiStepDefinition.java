package api.steps;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.rest.SerenityRest;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import api.WeatherApi;
import api.pojo.weather.CityWeather;
import utils.FileUtils;
import utils.ReportUtils;

public class ApiStepDefinition {

    private String cityIdList;
    private List<String> expectedCityNames;
    private Response response;

    WeatherApi weatherApi;

    @Given("I have a list of major international City IDs:")
    public void i_have_a_list_of_city_ids(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        this.cityIdList = rows.stream().map(row -> row.get("city_id")).collect(Collectors.joining(","));
        this.expectedCityNames = rows.stream().map(row -> row.get("city_name")).collect(Collectors.toList());
    }

    @When("I request the current weather from Weatherbit")
    public void i_request_current_weather() {
        response = weatherApi.getWeatherByCityIds(cityIdList);
    }

    @Then("the response should contain weather data for each city")
    public void verify_response() {
        response.then().statusCode(200);

        List<CityWeather> weatherList = response.jsonPath().getList("data", CityWeather.class);
        List<String> receivedCityNames = weatherList.stream()
                .map(CityWeather::getCityName)
                .collect(Collectors.toList());

        List<String> missingCities = new ArrayList<>();
        for (String expectedCity : expectedCityNames) {
            if (!receivedCityNames.contains(expectedCity)) {
                missingCities.add(expectedCity);
            }
        }

        if (!missingCities.isEmpty()) {
            Serenity.recordReportData().withTitle("MISSING CITIES DETECTED")
                    .andContents("The following cities were not found: " + missingCities);
        }

        Serenity.recordReportData().withTitle("Full Weather Table")
                .andContents(ReportUtils.generateWeatherTable(weatherList));

        if (!missingCities.isEmpty()) {
            throw new AssertionError("Test Failed: Some cities were missing from the response: " + missingCities);
        }
    }

    @When("I fetch weather data for coordinates Latitude {double} and Longitude {double}")
    public void fetchWeatherByCoordinates(Double lat, Double lon) {
        response = weatherApi.getWeatherByCoords(lat, lon);
    }

    @Then("the response should contain valid data for these coordinates")
    public void verifyCoordinateResponse() {
        response.then().statusCode(200);

        List<CityWeather> weatherList = response.jsonPath().getList("data", CityWeather.class);
        int count = response.jsonPath().getInt("count");

        assertThat("Data list size should be 1", weatherList, hasSize(1));
        assertThat("Count field should be 1", count, equalTo(1));

        CityWeather city = weatherList.get(0);
        Serenity.recordReportData().withTitle("Coordinate Search Result")
                .andContents(ReportUtils.buildCoordinateReport(city));
    }

    @When("I fetch current weather for all cities in {string}")
    public void fetchWeatherFromJsonFile(String fileName) throws Exception {
        String joinedIds = FileUtils.getCityIdsFromJson(fileName);
        weatherApi.getWeatherByCityIds(joinedIds);
    }

    @Then("I should identify and report the warmest city among them")
    public void identifyWarmestCity() {
        List<CityWeather> weatherList = SerenityRest.lastResponse().jsonPath().getList("data", CityWeather.class);

        CityWeather warmestCity = weatherList.stream()
                .max(Comparator.comparing(CityWeather::getTemp))
                .orElseThrow(() -> new RuntimeException("No data found"));

        Serenity.recordReportData().withTitle("Australian Capitals Temperature Analysis")
                .andContents(ReportUtils.buildWarmestCityReport(warmestCity));
    }

    @When("I identify the coldest US state using the metadata in {string}")
    public void identifyColdestFromCSV(String fileName) throws Exception {
        List<String> cityIds = FileUtils.getUsStateCityIdsFromCsv(fileName, 30);
        System.out.println("Total US States identified from metadata: " + cityIds.size());
        response = weatherApi.getWeatherByCityIds(String.join(",", cityIds));
    }

    @Then("the system should report the state with the lowest current temperature")
    public void reportColdestState() {
        List<CityWeather> weatherList = response.jsonPath().getList("data", CityWeather.class);

        CityWeather coldest = weatherList.stream()
                .min(Comparator.comparing(CityWeather::getTemp))
                .orElseThrow();

        String stateCode = response.jsonPath().getString(
                "data.find { it.city_name == '" + coldest.getCityName() + "' }.state_code");

        Serenity.recordReportData()
                .withTitle("Logistics Intelligence: Coldest US State")
                .andContents(ReportUtils.buildColdestStateReport(coldest, stateCode));
    }
}
