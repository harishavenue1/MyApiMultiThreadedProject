package com.api.cucumber.utils;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.runner.Request;

public class TokenUtils {

    private static final ThreadLocal<RequestSpecification> requestSpec = ThreadLocal.withInitial(RestAssured::given);

    public static String getToken(String serviceName) {
        try {
            RequestSpecification httpRequest = requestSpec.get();
            String token;

            httpRequest.and().param("grant_type", PropertyUtils.getProperty(serviceName + ".pingId.grant_type"));
            httpRequest.and().param("client_id", PropertyUtils.getProperty(serviceName + ".pingId.client_id"));
            httpRequest.and().param("client_secret", PropertyUtils.getProperty(serviceName + ".pingId.client_secret"));
            httpRequest.and().param("scope", PropertyUtils.getProperty(serviceName + ".pingId.scope"));
            Response response = httpRequest.post(PropertyUtils.getProperty(serviceName + ".pingId.url"));

            JsonPath jsonPathEvaluator = response.jsonPath();
            token = jsonPathEvaluator.get("access_token");
            return token;
        } finally {
            if (requestSpec.get() != null) {
                requestSpec.remove();
            }
        }
    }
}
