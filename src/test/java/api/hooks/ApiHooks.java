package api.hooks;

import io.cucumber.java.Before;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;
import net.serenitybdd.rest.SerenityRest;

public class ApiHooks {

    private EnvironmentVariables environmentVariables;

    @Before
    public void configureRest() {
        // Fetch properties once from serenity.conf
        String baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty("restapi.weather.baseurl");
        
        String apiKey = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty("api.key");
        
     // Build a specification that includes the URL and the API Key
        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)           // Use setBaseUri, NOT BasePath
                .addQueryParam("key", apiKey)  // Add the key once here
                .build();

        // Set this as the global default for all SerenityRest calls
        SerenityRest.setDefaultRequestSpecification(spec);
    }
    
	/*
	 * @Before(order = 2) // Runs after configureRest (default order is 0) public
	 * void checkApiHealth() { // We use a simple path that requires the API key
	 * (which is now in the default spec) // /current with a dummy city is usually
	 * the lightest 'up' check SerenityRest.given() .queryParam("city", "Sydney")
	 * .get("/current") .then() .statusCode(200); // If this fails, the whole
	 * scenario is marked as failed/skipped }
	 */
}