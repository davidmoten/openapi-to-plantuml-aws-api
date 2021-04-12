package com.github.davidmoten.apig.example;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.davidmoten.aws.helper.StandardRequestBodyPassThrough;

public class Handler {

    public String handle(Map<String, Object> input, Context context) {

        // expects full request body passthrough from api gateway integration
        // request
        StandardRequestBodyPassThrough request = StandardRequestBodyPassThrough.from(input);

        String name = request.queryStringParameter("name")
                .orElseThrow(() -> new IllegalArgumentException("parameter 'name' not found"));

        return "Hello " + name;

    }
}
