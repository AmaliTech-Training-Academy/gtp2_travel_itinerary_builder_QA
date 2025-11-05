package com.capstone.api_tests.tests;

import com.capstone.api_tests.base.BaseTest;
import com.capstone.api_tests.endpoints.TripEndpoints;
import com.capstone.api_tests.triphelpers.TripHelper;
import com.capstone.api_tests.triphelpers.TestInfoHolder;
import com.capstone.api_tests.testdata.TripTestData;
import com.capstone.api_tests.utils.TokenManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

@Epic("Travel Itinerary Builder")
@Feature("Trip Management APIs")
public class TripTest extends BaseTest {

    TripEndpoints trip = new TripEndpoints();
    String token = TokenManager.getToken();
    String tripId;


    void setupData() {
        // ✅ Create trip only for tests that need an existing trip
        if (!TestInfoHolder.isCreateTripTest()) {
            tripId = TripHelper.createTestTrip(token, "Accra");
        }
    }


    void cleanup() {
        if (tripId != null) {
            TripHelper.deleteTestTrip(token, tripId);
        }
    }

    private static Stream<Arguments> invalidTripProvider() {
        return Stream.of(
                Arguments.of(TripTestData.tripWithMissingDestination(), List.of(400,404)),
                Arguments.of(TripTestData.tripMissingDates("Nairobi"), List.of(400,422)),
                Arguments.of(TripTestData.invalidDateTrip("Accra"), List.of(400,422)),
                Arguments.of(TripTestData.largeTravelerTrip("Accra"), List.of(400,422))
        );
    }


    @Test
    @Story("TC101 - Create trip successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateTrip() {
        var tripData = TripTestData.validTripData("Paris");

        Response response = trip.createTrip(tripData, token);

        response.then()
                .statusCode(201)
                .body("success", is(true))
                .body("message", equalTo("Trip created successfully."));

        cleanup();
    }

    @Test
    @Story("TC102 - Get Trip by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetTripById() {

        setupData();
        trip.getTripById(token, tripId)
                .then()
                .statusCode(200)
                .body("success", is(true));

        cleanup();
    }

    @Test
    @Story("TC103 - Update Trip")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateTrip() {
        setupData();

        trip.updateTrip(token, tripId, TripTestData.updateTripData())
                .then()
                .statusCode(200);

        cleanup();
    }

    @Test
    @Story("TC104 - Delete Trip")
    @Severity(SeverityLevel.MINOR)
    public void testDeleteTrip() {
        setupData();

        trip.deleteTrip(token, tripId)
                .then()
                .statusCode(200);

        cleanup();
    }



    @Test
    @Story("TC105 - Get All Trips")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllTrips() {

        trip.getAllTrips(token, 1, 10)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }


    @Test
    @Story("TC106 - Get Trip Invite Link")
    @Severity(SeverityLevel.NORMAL)
    public void testGetInviteLink() {
        setupData();

        trip.getInviteLink(token, tripId)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.inviteLink", notNullValue());

        cleanup();
    }


    @Test
    @Story("TC107 - Add Trip Mates")
    @Severity(SeverityLevel.CRITICAL)
    public void testAddTripMates() {
        setupData();

        var payload = Map.of("emails", List.of(TripTestData.VALID_EMAIL));

        trip.addTripMates(token, tripId, payload)
                .then()
                .statusCode(200)
                .body("success", is(true));

        cleanup();
    }


    @Test
    @Story("TC108 - Revoke Trip Invite")
    @Severity(SeverityLevel.NORMAL)
    public void testRevokeTripInvite() {
        String email = TripTestData.VALID_EMAIL;

        setupData();
        // First add mate
        trip.addTripMates(token, tripId, Map.of("emails", List.of(email)))
                .then()
                .statusCode(200);

        // Now revoke
        trip.revokeInvite(token, tripId, email)
                .then()
                .statusCode(200);

        cleanup();
    }


    @Test
    @Story("TC109 - Create trip without destination")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateTripMissingDestination() {
        var payload = TripTestData.tripWithMissingDestination();

        trip.createTrip(payload, token)
                .then()
                .statusCode(anyOf(is(400), is(404)))
                .body("success", is(false));
    }

    @Test
    @Story("TC110 - Fail to create trip without dates")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateTripMissingDates() {
        var payload = TripTestData.tripMissingDates("Nairobi");

        trip.createTrip(payload, token)
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .body("success", is(false));
    }

    @Test
    @Story("TC111 - Fail to create trip with invalid date range")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateTripWithInvalidDates() {
        var payload = TripTestData.invalidDateTrip("Accra");

        trip.createTrip(payload, token)
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .body("success", is(false));
    }



    @Test
    @Story("TC112 - Delete already deleted or invalid Trip")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteTripNotFound() {
        setupData();

        trip.deleteTrip(token, tripId)
                .then()
                .statusCode(200); // first delete

        trip.deleteTrip(token, tripId)
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("message", containsString("Trip not found"));

        cleanup();
    }


    @Test
    @Story("TC113 - Fail to get trip without token")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetTripWithoutToken() {
        setupData();

        trip.getTripById(null, tripId)
                .then()
                .statusCode(anyOf(is(401), is(403)));

        cleanup();
    }

    @Test
    @Story("TC114 - Invalid trip ID format")
    @Severity(SeverityLevel.MINOR)
    public void testInvalidTripIdFormat() {

        trip.getTripById(token, TripTestData.INVALID_TRIP_ID)
                .then()
                .statusCode(anyOf(is(400), is(500)))
                .body("success", is(false));
    }


    @Test
    @Story("TC115 - Get trips with high page number")
    @Severity(SeverityLevel.MINOR)
    public void testGetTripsEmptyPage() {

        trip.getAllTrips(token, 999, 10)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content.size()", is(0));
    }


    @Test
    @Story("TC116 - Add same trip mate twice")
    @Severity(SeverityLevel.NORMAL)
    public void testAddDuplicateTripMate() {
        setupData();

        String email = TripTestData.VALID_EMAIL;
        var payload = Map.of("emails", List.of(email));

        trip.addTripMates(token, tripId, payload).then().statusCode(200);
        trip.addTripMates(token, tripId, payload)
                .then()
                .statusCode(anyOf(is(400), is(409)));

        cleanup();
    }


    @Test
    @Story("TC117 - Revoke invite for non-existing trip mate")
    @Severity(SeverityLevel.MINOR)
    public void testRevokeInviteForUnknownUser() {
        setupData();

        trip.revokeInvite(token, tripId, TripTestData.NON_EXIST_EMAIL)
                .then()
                .statusCode(anyOf(is(400), is(404)));

        cleanup();
    }


    @Test
    @Story("TC118 - Create trip with very large traveler count")
    @Severity(SeverityLevel.MINOR)
    public void testCreateTripLargeTravelerCount() {
        var payload = TripTestData.largeTravelerTrip("Accra");

        trip.createTrip(payload, token)
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }



}
