package com.capstone.api_tests.tests;

import com.capstone.api_tests.base.BaseTest;
import com.capstone.api_tests.endpoints.TripEndpoints;
import com.capstone.api_tests.triphelpers.TripHelper;
import com.capstone.api_tests.utils.TokenManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

@Epic("Travel Itinerary Builder")
@Feature("Trip Invite API")
public class TripInviteTests extends BaseTest {

    TripEndpoints trip = new TripEndpoints();
    String ownerToken = TokenManager.getToken();
    String tripId;
    String inviteCode;

    @BeforeEach
    void setup() {
        // Create trip as owner
        tripId = TripHelper.createTestTrip(ownerToken, "Berlin");

        // Generate invite link
        Response resp = trip.getInviteLink(ownerToken, tripId);
        resp.then().statusCode(200);

        String inviteLink = resp.jsonPath().getString("data.inviteLink");

        // Extract invite code from link
        inviteCode = inviteLink.split("token=")[1];
    }

    @AfterEach
    void cleanup() {
        TripHelper.deleteTestTrip(ownerToken, tripId);
    }

    @Test
    @Story("TC201 - Preview Trip Invite")
    @Severity(SeverityLevel.CRITICAL)
    public void testPreviewInvite() {
        trip.previewInvite(inviteCode)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.tripId", equalTo(tripId))
                .body("data.status", is("PENDING"));
    }

    @Test
    @Story("TC202 - Accept Trip Invite")
    @Severity(SeverityLevel.CRITICAL)
    public void testAcceptInvite() {
        String userToken = TokenManager.getAnotherUserToken(); // second user

        trip.acceptInvite(inviteCode, userToken)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("message", containsString("joined"));
    }

    @Test
    @Story("TC203 - Reject Trip Invite")
    @Severity(SeverityLevel.NORMAL)
    public void testRejectInvite() {
        String userToken = TokenManager.getAnotherUserToken();

        trip.rejectInvite(inviteCode, userToken)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("message", containsString("rejected"));
    }



}
