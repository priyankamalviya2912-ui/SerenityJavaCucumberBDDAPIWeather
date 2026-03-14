package utils;

import java.io.File;
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

public class FileUtils {
	private static String filePath = "src/test/resources/testdata/";

	public static String getCityIdsFromJson(String fileName) throws Exception {
		String filePath = "src/test/resources/testdata/" + fileName;
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, String>> cities = mapper.readValue(new File(filePath), new TypeReference<>() {
		});
		return cities.stream().map(city -> city.get("id")).collect(Collectors.joining(","));
	}

	// Logic from identifyColdestFromCSV
	public static List<String> getUsStateCityIdsFromCsv(String fileName, int limit) throws Exception {
		String filePath = "src/test/resources/testdata/" + fileName;
		Map<String, String> stateMap = new HashMap<>();
		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] line;
			reader.readNext(); // Skip header
			while ((line = reader.readNext()) != null) {
				if ("US".equalsIgnoreCase(line[3])) {
					stateMap.put(line[2], line[0]);
				}
				if (stateMap.size() >= limit)
					break;
			}
		}
		return new ArrayList<>(stateMap.values());
	}
}
