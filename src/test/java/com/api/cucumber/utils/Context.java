package com.api.cucumber.utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(Context.class);

    private final String REQUEST_KEY = "request";
    private final String RESPONSE_KEY = "response";
    private final String REQUEST_BODY_KEY = "requestBody";
    private final String PING_ID_TOKEN_KEY = "pingIdToken";

    public Context() {
        context.set(new HashMap<>());
        logger.debug("Context initialized for thread: {}", Thread.currentThread().getName());
    }

    public void init() {
        if (context.get() == null) {
            context.set(new HashMap<>());
            logger.debug("Context re-initialized for thread {}", Thread.currentThread().getName());
        }
    }

    public void setValue(String key, Object value) {
        init();
        context.get().put(key, value);
        logger.debug("Set {} for thread {}", key, Thread.currentThread().getName());
    }

    public <T> T getValue(String key) {
        init();
        return (T) context.get().get(key);
    }

    public void setRequest(RequestSpecification request) {
        setValue(REQUEST_KEY, request);
    }

    public RequestSpecification getRequest() {
        return getValue(REQUEST_KEY);
    }

    public void setResponse(Response response) {
        setValue(RESPONSE_KEY, response);
    }

    public Response getResponse() {
        return getValue(RESPONSE_KEY);
    }

    public void setRequestBody(String requestBody) {
        setValue(REQUEST_BODY_KEY, requestBody);
    }

    public String getRequestBody() {
        return getValue(REQUEST_BODY_KEY);
    }

    public void setPingIdToken(String token) {
        setValue(PING_ID_TOKEN_KEY, token);
    }

    public String getPingIdToken() {
        return getValue(PING_ID_TOKEN_KEY);
    }

    public void cleanUp() {
        logger.debug("Cleaning up context for thread {}: ", Thread.currentThread().getName());
        context.remove();
    }
}
