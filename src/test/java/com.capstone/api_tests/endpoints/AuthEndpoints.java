package com.capstone.api_tests.endpoints;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static com.capstone.api_tests.base.APIBaseTest.requestSpec;
import java.util.Map;

public class AuthEndpoints {

    private static final String BASE_PATH = "/auth";

    public Response register(Map<String, Object> payload) {
        return given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .post(BASE_PATH + "/register");
    }

    public Response login(Map<String, Object> payload) {
        return given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .post(BASE_PATH + "/login");
    }

    public Response forgotPassword(Map<String, Object> payload) {
        return given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .post(BASE_PATH + "/forgot-password");
    }

    public Response resetPassword(Map<String, Object> payload) {
        return given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .post(BASE_PATH + "/reset-password");
    }

    public Response refreshToken(String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(BASE_PATH + "/refresh-token");
    }

    public Response logout(String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .post(BASE_PATH + "/logout");
    }

    public Response logoutWithoutToken() {
        return given()
                .spec(requestSpec)
                .when()
                .post(BASE_PATH + "/logout");
    }

    public Response accessProtectedEndpoint(String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/trips");
    }

}
