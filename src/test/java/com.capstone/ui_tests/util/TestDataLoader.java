package com.capstone.ui_tests.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.provider.Arguments;

import java.io.InputStream;
import java.util.stream.Stream;

public class TestDataLoader {
    private static JsonNode rootNode;

    static {
        try (InputStream is = TestDataLoader.class.getResourceAsStream("/testdata/testdata.json")) {
            if (is == null) {
                throw new IllegalStateException("❌ testdata/testdata.json not found in resources");
            }
            ObjectMapper mapper = new ObjectMapper();
            rootNode = mapper.readTree(is);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load testdata.json", e);
        }
    }

    // --- USERS ---
    public static User getUser(String key) {
        JsonNode node = rootNode.path("users").path(key);
        return new User(node.get("email").asText(), node.get("password").asText());
    }

    // --- ERRORS ---
    public static String getErrorMessage(String key) {
        return rootNode.path("errors").path(key).asText();
    }

    // --- PARAMETERIZED DATA PROVIDER FOR NEGATIVE LOGIN ---
    public static Stream<Arguments> negativeLoginData() {
        return Stream.of(
                Arguments.of("invalid_login", "invalidcredentials"),
                Arguments.of("empty_email", "emptyFields"),
                Arguments.of("empty_password", "emptyFields"),
                Arguments.of("empty_credentials", "emptyFields")
        );
    }
}
