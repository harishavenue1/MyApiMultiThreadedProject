package com.api.cucumber.stepdefinition;

import com.api.cucumber.utils.Context;
import com.api.cucumber.utils.ScenarioStorage;
import com.api.cucumber.utils.ThreadLoggerFactory;
import com.api.cucumber.utils.ThreadMonitor;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);
    private final Context context;

    public Hooks(Context context) {
        this.context = context;
    }

    @Before
    public void setup(Scenario scenario) {
        try {
            ThreadMonitor.logThreadExecution(scenario.getName());
            long threadId = Thread.currentThread().getId();
            ScenarioStorage.setScenario(scenario);

            LOGGER.info("Starting scenario: {} on thread: {}", scenario.getName(), Thread.currentThread().getName());
            LOGGER.info("Setup complete for scenario: {} on thread {}", scenario.getName(), threadId);
        } catch (Exception e) {
            LOGGER.error("Error during setup: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to setup test environment", e);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            long threadId = Thread.currentThread().getId();
            ThreadLoggerFactory.cleanup();
            ScenarioStorage.clear();
            context.cleanUp();
            LOGGER.info("Tear down complete for scenario: {} on thread {}", scenario.getName(), threadId);
        } catch (Exception e) {
            LOGGER.error("Error during tear down: {}", e.getMessage(), e);
        }
    }
}
