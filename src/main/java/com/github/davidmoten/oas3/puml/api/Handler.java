package com.github.davidmoten.oas3.puml.api;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.davidmoten.oas3.puml.Converter;

import net.sourceforge.plantuml.code.TranscoderSmart;

public final class Handler implements RequestHandler<Map<String, Object>, String> {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {

        // when a null body is passed `input.get("body-json") returns a LinkedHashMap
        // not a String (assumes encoded parameters perhaps?)

        try {
            Object bodyJson = input.get("body-json");
            final String yaml;
            if (bodyJson instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) bodyJson;
                try {
                    JsonNode jsonNode = MAPPER.valueToTree(map);
                    yaml = new YAMLMapper().writeValueAsString(jsonNode);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            } else if (bodyJson instanceof String){
                yaml = (String) bodyJson;
                if (yaml == null || yaml.trim().length() == 0) {
                    throw new IllegalArgumentException("openapi definition cannot be empty");
                }
            } else {
                throw new IllegalArgumentException("unexpected body-json: " + bodyJson);
            }
            String puml = Converter.openApiToPuml(yaml);
            String encoded = new TranscoderSmart().encode(puml);
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
