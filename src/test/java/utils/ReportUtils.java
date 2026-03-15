package utils;

import java.util.List;

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

    public static String buildCoordinateReport(CityWeather city) {
        return String.format("Location: %s | Temp: %.1f°C | Condition: %s",
                city.getCityName(), city.getTemp(), city.getWeather().getDescription());
    }

    public static String buildWarmestCityReport(CityWeather warmestCity) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s | %-10s | %-15s\n", "City", "Temp", "Condition"));
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-15s | %-10.1f | %-15s %s\n",
                warmestCity.getCityName(), warmestCity.getTemp(),
                warmestCity.getWeather().getDescription(),
                warmestCity.getCityName() + " 🔥 [WARMEST]"));
        return "The warmest capital right now is " + warmestCity.getCityName() +
               " at " + warmestCity.getTemp() + "°C.\n\n" + sb.toString();
    }

    public static String buildColdestStateReport(CityWeather coldest, String stateCode) {
        return "COLDEST DETECTED STATE: " + stateCode + " (" + coldest.getCityName() + ")\n" +
               "TEMPERATURE: " + coldest.getTemp() + "°C\n" +
               "CONDITION: " + coldest.getWeather().getDescription();
    }
}
