package com.github.davidmoten.oas3.puml.api;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class HandlerTest {

    @Test
    public void testBadYaml() {
        try {
            Map<String, Object> input = new HashMap<>();
            input.put("body-json", "abcd");
            new Handler().handleRequest(input, null);
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().startsWith("BadRequest"));
        }

    }

}
