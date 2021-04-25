package com.github.davidmoten.oas3.puml.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.davidmoten.oas3.puml.Converter;

import net.sourceforge.plantuml.code.TranscoderSmart2;

public final class Handler {

    public String handle(Map<String, Object> input, Context context) {

        // expects full request body passthrough from api gateway integration
        // request

        // when a null body is passed `input.get("body-json") returns a LinkedHashMap
        // not a String (assumes encoded parameters perhaps?)

        Object bodyJson = input.get("body-json");
        if (!(bodyJson instanceof String)) {
            throw new IllegalArgumentException("openapi deinition cannot be empty");
        }
        String body = (String) bodyJson;
        if (body == null || body.trim().length() == 0) {
            throw new IllegalArgumentException("openapi definition cannot be empty");
        }
        String puml = Converter.openApiToPuml(body);
        try {
            String encoded = new TranscoderSmart2().encode(puml);
            return "{\"puml\":\"" + escapeForJson(puml) + "\",\n" //
                    + "\"encoded\":\"" + escapeForJson(encoded) + "\"}";
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String escapeForJson(String raw) {
        return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b")
                .replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
