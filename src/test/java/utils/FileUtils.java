package utils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

public class FileUtils {

    private static final String TEST_DATA_PATH = "src/test/resources/testdata/";

    public static String getCityIdsFromJson(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> cities = mapper.readValue(
                new File(TEST_DATA_PATH + fileName), new TypeReference<>() {});
        return cities.stream().map(city -> city.get("id")).collect(Collectors.joining(","));
    }

    public static List<String> getUsStateCityIdsFromCsv(String fileName, int limit) throws Exception {
        Map<String, String> stateMap = new HashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(TEST_DATA_PATH + fileName))) {
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
