package com.github.davidmoten.apig.example;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.davidmoten.oas3.puml.Converter;

public class Handler {

    public String handle(Map<String, Object> input, Context context) {

        // expects full request body passthrough from api gateway integration
        // request
        String body = (String) input.get("body");
        return Converter.openApiToPuml(body);

    }
}
