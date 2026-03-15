package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    private static final String TEST_DATA_PATH = "src/test/resources/testdata/";

    private FileUtils() {
        // utility class — no instantiation
    }

    public static String getCityIdsFromJson(String fileName) throws IOException {
        File file = new File(TEST_DATA_PATH + fileName);
        log.debug("Reading city IDs from JSON file: {}", file.getPath());
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> cities = mapper.readValue(file, new TypeReference<>() {});
        String joinedIds = cities.stream().map(city -> city.get("id")).collect(Collectors.joining(","));
        log.debug("Loaded {} cities from {}", cities.size(), fileName);
        return joinedIds;
    }

    public static List<String> getUsStateCityIdsFromCsv(String fileName, int limit) throws IOException, CsvValidationException {
        File file = new File(TEST_DATA_PATH + fileName);
        log.debug("Reading US state city IDs from CSV file: {}", file.getPath());
        Map<String, String> stateMap = new HashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                if ("US".equalsIgnoreCase(line[3])) {
                    stateMap.put(line[2], line[0]);
                }
                if (stateMap.size() >= limit) {
                    break;
                }
            }
        }
        log.debug("Loaded {} US state city IDs from {}", stateMap.size(), fileName);
        return new ArrayList<>(stateMap.values());
    }
}
