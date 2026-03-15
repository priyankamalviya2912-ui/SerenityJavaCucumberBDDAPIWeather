package api;

import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.rest.SerenityRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherApi extends PageObject {

    private static final Logger log = LoggerFactory.getLogger(WeatherApi.class);

    private Response response;

    @Step("Fetching current weather for city IDs: {0}")
    public Response getWeatherByCityIds(String cityIdList) {
        log.info("GET /current with cities: {}", cityIdList);
        response = SerenityRest.given()
                .queryParam("cities", cityIdList)
                .when().get("/current");
        log.debug("Response status: {}", response.getStatusCode());
        return response;
    }

    @Step("Fetching current weather by coordinates: lat={0}, lon={1}")
    public Response getWeatherByCoords(Double lat, Double lon) {
        log.info("GET /current with lat={}, lon={}", lat, lon);
        response = SerenityRest.given()
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .when().get("/current");
        log.debug("Response status: {}", response.getStatusCode());
        return response;
    }
}
