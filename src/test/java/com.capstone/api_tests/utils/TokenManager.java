package com.capstone.api_tests.utils;

import com.capstone.api_tests.endpoints.AuthEndpoints;
import com.capstone.api_tests.testdata.AuthTestData;
import io.restassured.response.Response;

import java.util.Map;

public class TokenManager {

    private static String token;          // Primary test user token
    private static String secondaryToken; // Second test user token

    public static String getToken() {
        if (token == null) {
            Map<String, Object> loginData = AuthTestData.validLoginData();
            Response response = new AuthEndpoints().login(loginData);
            token = response.jsonPath().getString("data.accessToken");
        }
        return token;
    }

    public static String getAnotherUserToken() {
        if (secondaryToken == null) {
            secondaryToken = getTokenFor("richmondnyarko123@gmail.com", "StrongPassword1");
        }
        return secondaryToken;
    }

    private static String getTokenFor(String email, String password) {
        Map<String, Object> loginData = Map.of(
                "email", email,
                "password", password
        );

        Response response = new AuthEndpoints().login(loginData);

        response.then().statusCode(200); // ensure login succeeded

        return response.jsonPath().getString("data.accessToken");
    }
}
