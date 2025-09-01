package com.api.cucumber.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLoggerFactory {
    private static final ConcurrentHashMap<Long, Logger> threadLoggers = new ConcurrentHashMap<>();
    private static final Object LOCK = new Object();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ThreadLoggerFactory.class);

    public static Logger getThreadLogger(String scenarioName) {
        long threadId = Thread.currentThread().getId();
        return threadLoggers.computeIfAbsent(threadId, k -> createLogger(threadId, scenarioName));
    }

    private static Logger createLogger(long threadId, String scenarioName) {
        try {
            synchronized (LOCK) {
                Path logsDir = Paths.get("logs");
                if (!Files.exists(logsDir)) {
                    Files.createDirectories(logsDir);
                }
            }

            String loggerName = String.format("Thread-%d", threadId);

            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

            Logger logger = loggerContext.getLogger(loggerName);

            FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
            fileAppender.setContext(loggerContext);

            String sanitizedScenarioName = scenarioName.replaceAll("[^a-zA-z0-9.-]", "_");
            String logFileName = String.format("logs/thread_%d_%s.log", threadId, sanitizedScenarioName);
            fileAppender.setFile(logFileName);

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%msg%n");
            encoder.start();

            fileAppender.setEncoder(encoder);
            fileAppender.start();

            logger.detachAndStopAllAppenders();
            logger.addAppender(fileAppender);
            logger.setLevel(Level.ALL);
            logger.setAdditive(false);
            logger.info("Created Logger for thread {} with file: {}", threadId, logFileName);
            return logger;
        } catch (Exception e) {
            LOGGER.error("Failed to create logger for thread {}: {}", threadId, e.getMessage());
            throw new RuntimeException("Failed to create logger", e);
        }
    }

    public static void cleanup() {
        long threadId = Thread.currentThread().getId();
        Logger logger = threadLoggers.remove(threadId);
        if (logger != null) {
            logger.detachAndStopAllAppenders();
        }
    }
}
