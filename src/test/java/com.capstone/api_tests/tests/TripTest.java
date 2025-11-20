package com.capstone.api_tests.tests;

import com.capstone.api_tests.base.APIBaseTest;
import com.capstone.api_tests.endpoints.TripEndpoints;
import com.capstone.api_tests.triphelpers.TripHelper;
import com.capstone.api_tests.triphelpers.TestInfoHolder;
import com.capstone.api_tests.testdata.TripTestData;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@Epic("Travel Itinerary Builder")
@Feature("Trip Management APIs")
public class TripTest extends APIBaseTest {

    TripEndpoints trip = new TripEndpoints();
    String tripId;


    @BeforeEach
    void init() {
        if (!TestInfoHolder.isCreateTripTest()) {
            tripId = TripHelper.createTestTrip(token, "Accra");
        }
    }

    @AfterEach
    void cleanup() {
        if (tripId != null) {
            TripHelper.deleteTestTrip(token, tripId);
            tripId = null;
        }
    }


    @Test
    @Story("TC101 - Get Trip by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetTripById() {
        trip.getTripById(token, tripId)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Story("TC102 - Update Trip")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateTrip() {
        trip.updateTrip(token, tripId, TripTestData.updateTripData())
                .then()
                .statusCode(200);
    }

    @Test
    @Story("TC103 - Delete Trip")
    @Severity(SeverityLevel.MINOR)
    public void testDeleteTrip() {
        trip.deleteTrip(token, tripId)
                .then()
                .statusCode(200);
    }


    @Test
    @Story("TC104 - Get Trip Invite Link")
    @Severity(SeverityLevel.NORMAL)
    public void testGetInviteLink() {
        trip.getInviteLink(token, tripId)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.inviteLink", notNullValue());
    }


    @Test
    @Story("TC105 - Add Trip Mates")
    @Severity(SeverityLevel.CRITICAL)
    public void testAddTripMates() {
        String email = TripTestData.uniqueEmail();
        var payload = Map.of("emails", List.of(email));

        trip.addTripMates(token, tripId, payload)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }


    @Test
    @Story("TC106 - Revoke Trip Invite")
    @Severity(SeverityLevel.NORMAL)
    public void testRevokeTripInvite() throws InterruptedException {
        String email = TripTestData.uniqueEmail();
        var payload = Map.of("emails", List.of(email));

        // First add mate
        trip.addTripMates(token, tripId, Map.of("emails", List.of(email)))
                .then()
                .statusCode(200);

        Thread.sleep(500);
        // Now revoke
        trip.revokeInvite(token, tripId, email)
                .then()
                .statusCode(200);
    }


    @Test
    @Story("TC107 - Delete already deleted or invalid Trip")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteTripNotFound() throws InterruptedException {
        trip.deleteTrip(token, tripId)
                .then()
                .statusCode(200); // first delete

        Thread.sleep(500);

        trip.deleteTrip(token, tripId)
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("message", containsString("Trip not found"));
    }


    @Test
    @Story("TC108 - Fail to get trip without token")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetTripWithoutToken() {
        trip.getTripById(null, tripId)
                .then()
                .statusCode(anyOf(is(401), is(403)));
    }

    @Test
    @Story("TC109 - Invalid trip ID format")
    @Severity(SeverityLevel.MINOR)
    public void testInvalidTripIdFormat() {

        trip.getTripById(token, TripTestData.INVALID_TRIP_ID)
                .then()
                .statusCode(anyOf(is(401), is(500)))
                .body("success", is(false));
    }


    @Test
    @Story("TC110 - Get trips with high page number")
    @Severity(SeverityLevel.MINOR)
    public void testGetTripsEmptyPage() {

        trip.getAllTrips(token, 999, 10)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content.size()", is(0));
    }


    @Test
    @Story("TC111 - Add same trip mate twice")
    @Severity(SeverityLevel.NORMAL)
    public void testAddDuplicateTripMate() throws InterruptedException {
        String email = TripTestData.VALID_EMAIL;
        var payload = Map.of("emails", List.of(email));

        trip.addTripMates(token, tripId, payload).then().statusCode(200);
        Thread.sleep(500);
        trip.addTripMates(token, tripId, payload)
                .then()
                .statusCode(anyOf(is(200), is(401)));
    }


    @Test
    @Story("TC112 - Revoke invite for non-existing trip mate")
    @Severity(SeverityLevel.MINOR)
    public void testRevokeInviteForUnknownUser() {
        trip.revokeInvite(token, tripId, TripTestData.NON_EXIST_EMAIL)
                .then()
                .statusCode(anyOf(is(401), is(404)));
    }

}
