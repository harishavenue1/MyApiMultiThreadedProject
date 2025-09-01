package com.api.cucumber.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ThreadMonitor.class);
    private static final Set<String> activeThreads = ConcurrentHashMap.newKeySet();

    public static void logThreadExecution(String scenarioName) {
        String threadName = Thread.currentThread().getName();
        activeThreads.add(threadName);
        logger.info("Scenario: {} running on thread: {}. Active threads: {}", scenarioName, threadName, activeThreads.size());
    }
}
