package api;

import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.util.EnvironmentVariables;

public class WeatherApi extends PageObject{

	
	Response response;

	@Step("Fetching current weather for city IDs: {0}")
	public Response getWeatherByCityIds(String cityIdList) {
		response = SerenityRest.given().queryParam("cities", cityIdList) 
				.when().get("/current");
		return response;

	}
	
	@Step("Fetching current weather by coordinates: {0}")
	public Response getWeatherByCoords(Double lat, Double lon) {
	    response = SerenityRest.given()
	            .queryParam("lat", lat)
	            .queryParam("lon", lon)
	            .when()
	            .get("/current");
	    return response;
	}
}
