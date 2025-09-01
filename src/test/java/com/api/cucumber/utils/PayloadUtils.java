package com.api.cucumber.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PayloadUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Context contextVar;

    public PayloadUtils(Context contextVar) {
        this.contextVar = contextVar;
    }

    public String readPayload(String payloadPath, String payloadKey) throws Exception
    {
        LOGGER.debug("Reading payload from: {}", payloadPath);
        try {
            if (payloadPath == null || payloadPath.trim().isEmpty()) {
                throw new IllegalArgumentException("Payload path cannot be null or empty");
            }

            Path path = Paths.get(payloadPath);
            if (!Files.exists(path)) {
                throw new Exception("Payload file not found: "+ payloadPath);
            }

            String content = Files.readString(path).trim();

            try {
                OBJECT_MAPPER.readTree(content);
            } catch (Exception e) {
                throw new Exception("Invalid Json in payload file: "+ payloadPath, e);
            }
            LOGGER.debug("Successfully read payload: {}", content);
            return content;
        } catch (Exception e) {
            LOGGER.error("Error reading payload: {}", e.getMessage(), e);
            throw new Exception("Failed to read payload from "+ payloadPath, e);
        }
    }

    public String removeElementFromPayload(String elementName, String payloadPath, String payload) throws Exception {
        LOGGER.debug("Removing element '{}' from payload", elementName);
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(payload);
            if (!(rootNode instanceof ObjectNode)) {
                throw new IllegalArgumentException("Payload must be a JSON object");
            }

            ((ObjectNode) rootNode).remove(elementName);

            String updatedPayload = OBJECT_MAPPER.writeValueAsString(rootNode);
            LOGGER.debug("Successfully removed element '{}' from payload", elementName);
            return updatedPayload;
        } catch (Exception e) {
            LOGGER.error("Error removing element from payload: {}", e.getMessage(), e);
            throw new Exception("Failed to remove element from payload", e);
        }
    }

    public void updatePayload(String varName, String varValue, String varType) throws Exception {
        LOGGER.debug("Updating payload - Name: {}, Value: {}, Type: {}", varName, varValue, varType);
        try {
            String currentPayload = contextVar.getRequestBody();
            if (currentPayload == null || currentPayload.trim().isEmpty()) {
                throw new IllegalStateException("No payload found in context");
            }

            JsonNode rootNode = OBJECT_MAPPER.readTree(currentPayload);
            if (!(rootNode instanceof ObjectNode)) {
                throw new IllegalArgumentException("Payload must be a JSON Object");
            }

            ObjectNode objectNode = (ObjectNode) rootNode;
            if ("String".equals(varType)) {
                objectNode.put(varName, varValue);
            } else if ("Number".equals(varType)) {
                try {
                    objectNode.put(varName, Double.parseDouble(varValue));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number format: "+ varValue);
                }
            } else if ("Boolean".equals(varType)) {
                objectNode.put(varName, Boolean.parseBoolean(varValue));
            } else {
                throw new Exception("Unsupported variable type: "+ varType);
            }

            String updatedPayload = OBJECT_MAPPER.writeValueAsString(objectNode);
            contextVar.setRequestBody(updatedPayload);
            LOGGER.debug("Updated payload: {}", updatedPayload);
        } catch (Exception e) {
            LOGGER.error("Error updating payload: {}", e.getMessage(), e);
            throw new Exception("Failed to update payload", e);
        }
    }

    public boolean isValidJson(String jsonString) {
        try {
            OBJECT_MAPPER.readTree(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
