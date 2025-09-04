package com.api.cucumber.utils;

import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioStorage {

    private static final ThreadLocal<Scenario> currentScenario = new ThreadLocal<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioStorage.class);

    public static void setScenario(Scenario scenario) {
        if (scenario != null) {
            currentScenario.set(scenario);
            LOGGER.debug("Scenario set for thread: {}", Thread.currentThread().getName());
        } else {
            LOGGER.warn("Attempted to set a null scenario for thread: {}", Thread.currentThread().getName());
        }
    }

    public static Scenario getScenario() {
        Scenario scenario = currentScenario.get();
        if (scenario == null) {
            LOGGER.warn("Scenario is null for thread: {}", Thread.currentThread().getName());
        }
        return scenario;
    }

    public static void clear() {
        currentScenario.remove();
        LOGGER.debug("Scenario removed for thread: {}", Thread.currentThread().getName());
    }
}
