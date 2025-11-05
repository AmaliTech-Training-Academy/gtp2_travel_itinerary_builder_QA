package com.capstone.api_tests.endpoints;

import com.capstone.api_tests.base.BaseTest;
import io.restassured.response.Response;
import java.util.Map;

import static com.capstone.api_tests.base.BaseTest.requestSpec;
import static io.restassured.RestAssured.given;

public class TripEndpoints {

    private static final String BASE_PATH = "/trips";

    public Response createTrip(Map<String, Object> payload, String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .when()
                .post(BASE_PATH);
    }

    public Response getAllTrips(String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(BASE_PATH);
    }

    public Response getTripById(String token, String tripId) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(BASE_PATH + "/" + tripId);
    }

    public Response updateTrip(String token, String tripId, Map<String, Object> payload) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .when()
                .put(BASE_PATH + "/" + tripId);
    }

    public Response deleteTrip(String token, String tripId) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(BASE_PATH + "/" + tripId);
    }

    public Response getAllTrips(String token, int page, int limit) {
        return given()
                .spec(BaseTest.requestSpec)
                .queryParam("page", page)
                .queryParam("limit", limit)
                .auth().oauth2(token)
                .when()
                .get(BASE_PATH);
    }

    public Response getInviteLink(String token, String tripId) {
        return given()
                .spec(BaseTest.requestSpec)
                .auth().oauth2(token)
                .when()
                .get(BASE_PATH + "/" + tripId + "/invite-link");
    }


    public Response addTripMates(String token, String tripId, Object payload) {
        return given()
                .spec(BaseTest.requestSpec)
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(BASE_PATH + "/" + tripId + "/invites");
    }



    public Response revokeInvite(String token, String tripId, String email) {
        return given()
                .spec(BaseTest.requestSpec)
                .auth().oauth2(token)
                .queryParam("email", email)
                .when()
                .delete(BASE_PATH + "/" + tripId + "/invites");
    }


    public Response previewInvite(String code) {
        return given()
                .spec(BaseTest.requestSpec)
                .get("/trips/invite?token=" + code);
    }

    public Response acceptInvite(String code, String token) {
        return given()
                .spec(BaseTest.requestSpec)
                .header("Authorization", "Bearer " + token)
                .post("/trips/invite/accept?token=" + code);
    }

    public Response rejectInvite(String code, String token) {
        return given()
                .spec(BaseTest.requestSpec)
                .header("Authorization", "Bearer " + token)
                .post("/trips/invite/reject?token=" + code);
    }



}
