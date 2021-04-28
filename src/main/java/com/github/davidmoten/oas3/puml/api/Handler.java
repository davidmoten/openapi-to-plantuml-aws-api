package com.github.davidmoten.oas3.puml.api;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.davidmoten.oas3.puml.Converter;

import net.sourceforge.plantuml.code.TranscoderSmart2;

public final class Handler {

    public String handle(Map<String, Object> input, Context context) {

        // expects full request body passthrough from api gateway integration
        // request

        // when a null body is passed `input.get("body-json") returns a LinkedHashMap
        // not a String (assumes encoded parameters perhaps?)

        try {
            Object bodyJson = input.get("body-json");
            if (!(bodyJson instanceof String)) {
                throw new IllegalArgumentException("openapi definition cannot be empty");
            }
            String body = (String) bodyJson;
            if (body == null || body.trim().length() == 0) {
                throw new IllegalArgumentException("openapi definition cannot be empty");
            }
            final String yaml;
            if (body.trim().startsWith("{")) {
                JsonNode jsonNodeTree;
                try {
                    jsonNodeTree = new ObjectMapper().readTree(body);
                    yaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            } else {
                yaml = body;
            }
            String puml = Converter.openApiToPuml(yaml);
            String encoded = new TranscoderSmart2().encode(puml);
            return "{\"puml\":\"" + escapeForJson(puml) + "\",\n" //
                    + "\"encoded\":\"" + escapeForJson(encoded) + "\"}";
        } catch (Throwable e) {
            // thrown error json created by lambda infrastructure is invalid in testing so building it ourselves:
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("errorMessage", "Bad Request: " + String.valueOf(e.getMessage()));
            node.put("errorType", e.getClass().getName());
            ArrayNode array = node.putArray("stackTrace");
            for (String line: ExceptionUtils.getStackTrace(e).split("\n")) {
                array.add(line);
            }
            try {
                return mapper.writeValueAsString(node);
            } catch (JsonProcessingException e1) {
                // shouldn't happen fingers crossed
                throw new RuntimeException(e1);
            }
        }
    }

    private static String escapeForJson(String raw) {
        return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b")
                .replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
