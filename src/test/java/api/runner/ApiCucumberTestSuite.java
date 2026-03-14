package api.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import org.junit.platform.suite.api.*;
import org.junit.platform.suite.api.*;
import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
//@IncludeTags("AC4")
@SelectPackages	("features.weather")
//@SelectClasspathResource("/features/nsw")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty,timeline:build/test-results/timeline")
public class ApiCucumberTestSuite {
}
