package api.hooks;

import io.cucumber.java.Before;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;
import net.serenitybdd.rest.SerenityRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiHooks {

    private static final Logger log = LoggerFactory.getLogger(ApiHooks.class);

    private EnvironmentVariables environmentVariables;

    @Before
    public void configureRest() {
        String baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty("restapi.weather.baseurl");

        String apiKey = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty("api.key");

        log.info("Configuring RestAssured base URL: {}", baseUrl);

        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addQueryParam("key", apiKey)
                .build();

        SerenityRest.setDefaultRequestSpecification(spec);
        log.debug("Default request specification set successfully");
    }
}
