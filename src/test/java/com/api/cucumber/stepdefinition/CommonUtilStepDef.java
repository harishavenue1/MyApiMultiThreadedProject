package com.api.cucumber.stepdefinition;

import com.api.cucumber.utils.Context;
import com.api.cucumber.utils.PropertyUtils;
import com.api.cucumber.utils.TokenUtils;
import io.cucumber.java.After;
import io.cucumber.java.an.E;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import io.restassured.path.json.JsonPath;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.apache.commons.io.IOUtils;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static io.restassured.RestAssured.given;

public class CommonUtilStepDef {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtilStepDef.class);
    private final Context context;

    @After
    public void tearDown() {
        context.cleanUp();
        LOGGER.info("Context removed in after method of CommonStepDef");
    }

    public CommonUtilStepDef(Context context) {
        this.context = context;
    }

    @Given("User Creates pingId token for service {string")
    public void createPingIdToken(String serviceName) {
        try {
            String pingIdTokenValue = TokenUtils.getToken(serviceName);
            context.setPingIdToken(pingIdTokenValue);
            LOGGER.info("pingId token created for service " + serviceName + " : " + pingIdTokenValue);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating pingId token for service " + serviceName + " : " + e.getMessage());
            throw new RuntimeException("Failed to create token", e);
        }
    }

    @Then("User loads properties for {string} API")
    public void loadProperties(String serviceName) {
        try {
            PropertyUtils.loadProperties(serviceName);
            LOGGER.info("Properties loaded for service " + serviceName);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while loading properties for service " + serviceName + " : " + e.getMessage());
            throw e;
        }
    }

    @When("User makes a POST call to endpoint {string}")
    public void postCall(String endpoint) {
        try {
            LOGGER.info("Making POST request to endpoint {}", endpoint);
            String transactionId = UUID.randomUUID().toString();

            RequestSpecification requestSpecification = context.getRequest();;

            requestSpecification.
                    relaxedHTTPSValidation()
                    .header("Content-Type", "application/json")
                    .header("transactionId", transactionId)
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer "+ context.getPingIdToken())
                    .body(context.getRequestBody());

            LOGGER.debug("Request body: {}", context.getRequestBody());
            String resolvedPath = PropertyUtils.getProperty(endpoint);
            Response response = requestSpecification.post(resolvedPath);

            context.setResponse(response);

            LOGGER.info("Response received: {}", response.getBody().asString());
            LOGGER.info("Response status code: {}", response.getStatusCode());

        } catch (Exception e) {
            LOGGER.error("Exception occurred while setting request body for POST call: " + e.getMessage());
            throw new RuntimeException("Failed to make POST request", e);
        }
    }

    @Then("User Validates response matching the {string} schema")
    public void validateResponseSchema(String endPoint) {
        try {
            LOGGER.info("Validating response schema for {}", endPoint);

            Response response = context.getResponse();
            String schemaFileName = endPoint + ".json";
            String schemaContent = "";//loadSchemaFile(schemaFileName);

            if (schemaContent == null || schemaContent.isEmpty()) {
                LOGGER.error("Unable to load schema content for {}",schemaFileName);
                throw new RuntimeException("Unable to load schema content for " + schemaFileName);
            }
            LOGGER.info("Successfully loaded schema file: {} ", schemaFileName);

            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schemaContent));
            LOGGER.info("Response schema validated successfully for endpoint {} ", endPoint);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while validating response schema for endpoint {}: {} ", endPoint, e.getMessage(), e);
            throw new RuntimeException("Failed to validate response schema", e);
        }
    }

    @When("User makes a {string} {string} call with {string} to endpoint {string}")
    public void makeHttpCall(String type, String httpType, String payload, String endpoint) {
        try {
            if ("JSON".equals(type)) {
                LOGGER.info("Making {} request to endpoint {}", httpType, endpoint);

                RequestSpecification requestSpecification = context.getRequest();
                if (requestSpecification == null) {
                    throw new RuntimeException("Request object is null");
                }

                Object payloadValue = context.getValue(payload);
                if (payloadValue == null) {
                    throw new RuntimeException("Payload value is null");
                }

                requestSpecification.body(payloadValue);

                String resolvedEndpoint = PropertyUtils.getProperty(endpoint);
                Response response = null;//executeRequest(requestSpecification, httpType, resolvedEndpoint);

                if (response != null && response.getBody() != null) {
                    LOGGER.debug("Response body: {}", response.getBody().prettyPrint());
                }

                context.setResponse(response);

                //addToReport(requestSpecification, response, resolvedEndpoint);

                LOGGER.info("Successfully made {} request to {}", httpType, resolvedEndpoint);
            } else {
                LOGGER.warn("{} {} is not implemented", type, httpType);
            }
        } catch (Exception e) {
            LOGGER.error("Error making http call: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to make http call", e);
        }
    }
}
