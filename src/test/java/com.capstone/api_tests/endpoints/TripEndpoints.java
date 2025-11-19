package com.capstone.api_tests.endpoints;

import com.capstone.api_tests.base.APIBaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;

import static com.capstone.api_tests.base.APIBaseTest.requestSpec;
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
                .spec(APIBaseTest.requestSpec)
                .queryParam("page", page)
                .queryParam("limit", limit)
                .auth().oauth2(token)
                .when()
                .get(BASE_PATH);
    }

    public Response getInviteLink(String token, String tripId) {
        return given()
                .spec(APIBaseTest.requestSpec)
                .auth().oauth2(token)
                .when()
                .get(BASE_PATH + "/" + tripId + "/invite-link");
    }


    public Response addTripMates(String token, String tripId, Object payload) {
        return given()
                .spec(APIBaseTest.requestSpec)
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(BASE_PATH + "/" + tripId + "/invites");
    }



    public Response revokeInvite(String token, String tripId, String email) {
        return given()
                .spec(APIBaseTest.requestSpec)
                .auth().oauth2(token)
                .queryParam("email", email)
                .when()
                .delete(BASE_PATH + "/" + tripId + "/invites");
    }


    public Response previewInvite(String code) {
        return given()
                .spec(APIBaseTest.requestSpec)
                .get("/trips/invite?token=" + code);
    }

    public Response acceptInvite(String inviteCode, String token, String email) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\"}")
                .when()
                .post("/trips/invite/" + inviteCode + "/accept");
    }

    public Response rejectInvite(String inviteCode, String token, String email) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\"}")
                .when()
                .post("/trips/invite/" + inviteCode + "/reject");
    }





}
