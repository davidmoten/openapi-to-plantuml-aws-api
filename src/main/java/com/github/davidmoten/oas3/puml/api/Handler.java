package com.github.davidmoten.oas3.puml.api;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.davidmoten.oas3.puml.Converter;

import net.sourceforge.plantuml.code.TranscoderSmart2;

public final class Handler implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {

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
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("BadRequest: " + e.getMessage(), e);
        } catch (Throwable e) {
            throw new RuntimeException("ServerException: " + e.getMessage(), e);
        }
    }

    private static String escapeForJson(String raw) {
        return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b")
                .replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
