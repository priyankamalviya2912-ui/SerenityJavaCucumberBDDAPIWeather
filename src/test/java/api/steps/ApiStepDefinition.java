package api.steps;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.rest.SerenityRest;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import api.WeatherApi;
import api.pojo.weather.CityWeather;

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

		List<String> receivedCityNames = weatherList.stream().map(CityWeather::getCityName)
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

		StringBuilder report = new StringBuilder();
		report.append(String.format("%-20s | %-10s | %-15s\n", "City", "Temp", "Description"));
		report.append("------------------------------------------------------------\n");

		for (CityWeather city : weatherList) {
			report.append(String.format("%-20s | %-10.1f | %-15s\n", city.getCityName(), city.getTemp(),
					city.getWeather().getDescription()));
		}

		Serenity.recordReportData().withTitle("Full Weather Table").andContents(report.toString());

		if (!missingCities.isEmpty()) {
			throw new AssertionError("Test Failed: Some cities were missing from the response: " + missingCities);
		}
		// restAssuredThat(response -> response.body("data.city_name",
		// containsInAnyOrder(expectedCityNames.toArray())));
	}

	@When("I fetch weather data for coordinates Latitude {double} and Longitude {double}")
	public void fetchWeatherByCoordinates(Double lat, Double lon) {

		response = weatherApi.getWeatherByCoords(lat, lon);
	}

	@Then("the response should contain valid data for these coordinates")
	public void verifyCoordinateResponse() {

		response.then().statusCode(200);

		// 2. Map to POJO (This is your single source of truth)
		List<CityWeather> weatherList = response.jsonPath().getList("data", CityWeather.class);
		int count = response.jsonPath().getInt("count");

		assertThat("Data list size should be 1", weatherList, hasSize(1));
		assertThat("Count field should be 1", count, equalTo(1));

		// 4. Safe Extraction: Since we passed the hasSize(1) check, this is now safe
		CityWeather city = weatherList.get(0);

		// 5. Reporting
		String reportContent = String.format("Location: %s | Temp: %.1f°C | Condition: %s", city.getCityName(),
				city.getTemp(), city.getWeather().getDescription());

		Serenity.recordReportData().withTitle("Coordinate Search Result").andContents(reportContent);
	}

	@When("I fetch current weather for all cities in {string}")
	public void fetchWeatherFromJsonFile(String fileName) throws Exception {
		// 1. Path to your file in src/test/resources
		String filePath = "src/test/resources/testdata/" + fileName;
		String content = new String(Files.readAllBytes(Paths.get(filePath)));

		// 2. Parse the JSON to extract IDs
		// We use Jackson's ObjectMapper to read the list of Maps
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, String>> cities = mapper.readValue(content, new TypeReference<List<Map<String, String>>>() {
		});

		// 3. Join IDs with commas: "2063523,2073124,..."
		String joinedIds = cities.stream().map(city -> city.get("id")).collect(Collectors.joining(","));

		// 4. Call the API
		weatherApi.getWeatherByCityIds(joinedIds);

	}

	@Then("I should identify and report the warmest city among them")
	public void identifyWarmestCity() {
		List<CityWeather> weatherList = SerenityRest.lastResponse().jsonPath().getList("data", CityWeather.class);

		// Find the warmest city using Java Streams
		CityWeather warmestCity = weatherList.stream().max(Comparator.comparing(CityWeather::getTemp))
				.orElseThrow(() -> new RuntimeException("No data found"));

		// Build the report
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-15s | %-10s | %-15s\n", "City", "Temp", "Condition"));
		sb.append("------------------------------------------\n");

		String isWarmest = warmestCity.getCityName() + " 🔥 [WARMEST]";
		sb.append(String.format("%-15s | %-10.1f | %-15s %s\n", warmestCity.getCityName(), warmestCity.getTemp(),
				warmestCity.getWeather().getDescription(), isWarmest));

		Serenity.recordReportData().withTitle("Australian Capitals Temperature Analysis")
				.andContents("The warmest capital right now is " + warmestCity.getCityName() + " at "
						+ warmestCity.getTemp() + "°C.\n\n" + sb.toString());
	}

	@When("I identify the coldest US state using the metadata in {string}")
	public void identifyColdestFromCSV(String fileName) throws Exception {
		String filePath = "src/test/resources/testdata/" + fileName;

		Map<String, String> stateToCityIdMap = new HashMap<>();

		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] line;
			reader.readNext(); // Skip header
			while ((line = reader.readNext()) != null) {
				String cityId = line[0];
				String stateCode = line[2];
				String countryCode = line[3];

				if ("US".equalsIgnoreCase(countryCode)) {
					stateToCityIdMap.put(stateCode, cityId);
				}

				// Optimization: Stop after we have 30 states to stay within API limits
				if (stateToCityIdMap.size() >= 30)
					break;
			}
		}
		
		// Log how many unique states were found for transparency
	    System.out.println("Total US States identified from metadata: " + stateToCityIdMap.size());
		
		
		// 2. Join the collected IDs
	    String joinedIds = String.join(",", stateToCityIdMap.values());
	    
	 // 3. Call the API (using the same 'ids' method we built)
	   response= weatherApi.getWeatherByCityIds(joinedIds);

	}
	
	@Then("the system should report the state with the lowest current temperature")
	public void reportColdestState() {
	    List<CityWeather> weatherList = response.jsonPath().getList("data", CityWeather.class);

	    // Find the minimum temperature
	    CityWeather coldest = weatherList.stream()
	            .min(Comparator.comparing(CityWeather::getTemp))
	            .orElseThrow();

	    // Map the result to the state code (Logistics Manager needs the State, not just the city)
	    String stateResult = response.jsonPath().getString("data.find { it.city_name == '" + coldest.getCityName() + "' }.state_code");

	    Serenity.recordReportData()
	            .withTitle("Logistics Intelligence: Coldest US State")
	            .andContents("COLDEST DETECTED STATE: " + stateResult + " (" + coldest.getCityName() + ")\n" +
	                         "TEMPERATURE: " + coldest.getTemp() + "°C\n" +
	                         "CONDITION: " + coldest.getWeather().getDescription());
	}
}