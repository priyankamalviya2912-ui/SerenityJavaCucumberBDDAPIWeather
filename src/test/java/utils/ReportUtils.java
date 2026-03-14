package utils;

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

public class ReportUtils {
	
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

}
