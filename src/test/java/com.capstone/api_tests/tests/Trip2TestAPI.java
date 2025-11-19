package com.capstone.api_tests.tests;

import com.capstone.api_tests.base.APIBaseTest;
import com.capstone.api_tests.endpoints.TripEndpoints;
import com.capstone.api_tests.testdata.TripTestData;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

@Epic("Travel Itinerary Builder")
@Feature("Trip Management APIs 2")
public class Trip2TestAPI extends APIBaseTest {
    TripEndpoints trip = new TripEndpoints();
    String tripId;

    @Test
    @Story("TC113 - Create trip successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateTrip() {
        var tripData = TripTestData.validTripData("Paris");

        Response response = trip.createTrip(tripData, token);

        response.then()
                .statusCode(201)
                .body("success", is(true))
                .body("message", equalTo("Trip created successfully."));
    }

    @Test
    @Story("TC114 - Get All Trips")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllTrips() {

        trip.getAllTrips(token, 1, 10)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }


    private static Stream<Arguments> invalidTripProvider() {
        return Stream.of(
                Arguments.of(TripTestData.tripWithMissingDestination(), List.of(401,404)),
                Arguments.of(TripTestData.tripMissingDates("Nairobi"), List.of(400,401)),
                Arguments.of(TripTestData.invalidDateTrip("Accra"), List.of(400,422)),
                Arguments.of(TripTestData.largeTravelerTrip("Accra"), List.of(401,404))
        );
    }


    @ParameterizedTest
    @MethodSource("invalidTripProvider")
    @Story("TC115 - Validate invalid trip creation scenarios")
    @Severity(SeverityLevel.CRITICAL)
    void testInvalidTripCreation(Map<String, Object> payload, List<Integer> expectedStatusCodes) {
        trip.createTrip(payload, token)
                .then()
                .statusCode(anyOf(
                        expectedStatusCodes.stream()
                                .map(Matchers::is)
                                .toArray(size -> new Matcher[size])
                ))
                .body("success", is(false));
    }

    
}
