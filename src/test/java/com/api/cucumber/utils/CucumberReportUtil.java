package com.api.cucumber.utils;

import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.presentation.PresentationMode;
import net.masterthought.cucumber.sorting.SortingMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CucumberReportUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberReportUtil.class);

    public static void afterSuite() {
        generateOfflineReport("target/cucumber.json");
    }

    public static void generateOfflineReport(String txtCucumberJsonReportPath) {
        List<String> reports = new LinkedList<>();
        reports.add(txtCucumberJsonReportPath);
        generateCucumberReporting(reports);
    }

    public static void generateCucumberReporting(List<String> jsonFiles) {
        try {
            String outputFolderPath = "target";
            File reportOutputDirectory = new File(outputFolderPath);

            String projectName = "Name";
            Configuration configuration = new Configuration(reportOutputDirectory, projectName);
            configuration.setQualifier("AutomationReport", "");
            configuration.addClassifications("Environment", System.getProperty("env", "QA"));
            configuration.addClassifications("TimeStamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            configuration.setSortingMethod(SortingMethod.NATURAL);
            configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
            configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
            configuration.setTrendsStatsFile(new File(outputFolderPath + File.separator + "trends.json"));

            ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
            reportBuilder.generateReports();

        } catch (Exception e) {
            LOGGER.error("Error while generating cucumber reporting", e);
        }
    }

    public static void addRequestResponseToHtmlReport(QueryableRequestSpecification request, Response response, String baseUrl) {
        try {
            Scenario scenario = ScenarioStorage.getScenario();
            if (scenario == null) {
                LOGGER.error("Scenario is null for thread: {}", Thread.currentThread().getName());
                return;
            }

            Logger logger = ThreadLoggerFactory.getThreadLogger(scenario.getName());
            StringBuilder logMessage = new StringBuilder();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            logMessage.append("TimeStamp: ")
                    .append(LocalDateTime.now().format(formatter))
                    .append("\n\n");

            if (baseUrl != null) {
                logMessage.append("Base URL: ")
                        .append(baseUrl)
                        .append("\n\n");
            }

            if (request != null) {
                logMessage.append("Request:\n")
                        .append(request.getMethod())
                        .append(" ")
                        .append(request.getURI())
                        .append("\n\n");

                if (request.getHeaders().size() > 0) {
                    logMessage.append("Request Headers:\n")
                            .append(request.getHeaders().toString())
                            .append("\n\n");
                }

                if (request.getBody() != null) {
                    logMessage.append("Request Body:\n")
                            .append(request.getBody().toString())
                            .append("\n\n");
                }
            }

            if (response != null) {
                logMessage.append("Response:\n")
                        .append(response.getStatusLine())
                        .append("\n\n");

                if (response.getHeaders().size() > 0) {
                    logMessage.append("Response Headers:\n")
                            .append(response.getHeaders().toString())
                            .append("\n\n");
                }

                if (response.getBody() != null) {
                    logMessage.append("Response Body:\n")
                            .append(response.getBody().prettyPrint())
                            .append("\n\n");
                }
            }

            scenario.log(logMessage.toString());
            logger.info(logMessage.toString());
            LOGGER.debug("Successfully added request/response to report for thread: {}", Thread.currentThread().getName());

        } catch (Exception e) {
            LOGGER.error("Error adding to HTML report for thread {}: {}", Thread.currentThread().getName(), e.getMessage(), e);
        }
    }
}
