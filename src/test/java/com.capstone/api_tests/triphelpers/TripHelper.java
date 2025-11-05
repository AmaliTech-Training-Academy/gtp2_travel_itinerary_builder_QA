package com.capstone.api_tests.triphelpers;

import com.capstone.api_tests.endpoints.TripEndpoints;
import com.capstone.api_tests.testdata.TripTestData;
import io.restassured.response.Response;

public class TripHelper {

    private static final TripEndpoints trip = new TripEndpoints();

    public static String createTestTrip(String token, String destination) {
        var data = TripTestData.validTripData(destination);

        Response resp = trip.createTrip(data, token);

        resp.then().statusCode(201);

        return resp.jsonPath().getString("data.id");
    }

    public static void deleteTestTrip(String token, String tripId) {
        if (tripId != null && !tripId.isEmpty()) {
            trip.deleteTrip(token, tripId)
                    .then()
                    .statusCode(200);
        }
    }
}
