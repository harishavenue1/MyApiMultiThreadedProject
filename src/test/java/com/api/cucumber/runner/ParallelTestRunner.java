package com.api.cucumber.runner;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;


@Suite
@IncludeEngines("cucumber")
@SuiteDisplayName("Cucumber Platform Suite")
@SelectDirectories("src/test/resources/features/profile")
@ConfigurationParameters({
        @ConfigurationParameter(key=GLUE_PROPERTY_NAME, value = "com.api.cucumber.stepdefinition"),
        @ConfigurationParameter(key=PLUGIN_PROPERTY_NAME, value = "pretty," +
                "html:target/cucumber-reports/cucumber-html-report.html," +
                "json:target/cucumber-reports/cucumber.json" +
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:" +
                "rerun:target/rerun.txt"),
        @ConfigurationParameter(key=FEATURES_PROPERTY_NAME, value = "src/test/resources/features/profiles"),
        @ConfigurationParameter(key=PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME, value = "true"),
        @ConfigurationParameter(key=PARALLEL_CONFIG_STRATEGY_PROPERTY_NAME, value = "fixed"),
})
public class ParallelTestRunner {
}
