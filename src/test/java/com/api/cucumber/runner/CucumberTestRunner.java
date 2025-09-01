package com.api.cucumber.runner;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.restassured.config.LogConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Cucumber.class)
@CucumberOptions(
        features="src/test/resources/features/profile",
        glue = {"com.api.cucumber.stepdefinition"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "rerun:target/failed_scenarios.txt"
        },
        tags = "@profiles"
)

public class CucumberTestRunner extends LogConfig {

    private static final Logger logger = LoggerFactory.getLogger(CucumberTestRunner.class);

    @BeforeClass
    public static void setup() {
        logger.info("starting test execution");
    }

    @AfterClass
    public static void tearDown() {
        logger.info("finishing test execution");
    }
}
