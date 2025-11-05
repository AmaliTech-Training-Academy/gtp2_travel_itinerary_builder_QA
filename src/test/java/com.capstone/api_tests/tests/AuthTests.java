package com.capstone.api_tests.tests;

import com.capstone.api_tests.base.BaseTest;
import com.capstone.api_tests.endpoints.AuthEndpoints;
import com.capstone.api_tests.testdata.AuthTestData;
import com.capstone.api_tests.utils.TokenManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.Matchers.*;

@Epic("Travel Itinerary Builder")
@Feature("Authentication APIs")
public class AuthTests extends BaseTest {

    AuthEndpoints auth = new AuthEndpoints();

    // ------------------- Registration -------------------
    @ParameterizedTest(name = "TC001 - Verify registration fails with invalid OTP [{index}]")
    @MethodSource("com.capstone.api_tests.testdata.AuthTestData#validRegistrationDataWithWrongOTPStream")
    @Severity(SeverityLevel.CRITICAL)
    public void testRegistrationFailsWithInvalidOtp(String firstName, String lastName, String email, String password, String otp) {
        var registerData = AuthTestData.validRegistrationDataWithWrongOTP();

        Response response = auth.register(registerData);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", containsStringIgnoringCase("invalid"));
    }

    // ------------------- Login -------------------
    @ParameterizedTest(name = "TC002-TC010 - Login Test Case [{index}]")
    @MethodSource("com.capstone.api_tests.testdata.AuthTestData#loginTestCases")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginParameterized(
            java.util.Map<String, Object> loginData,
            int expectedStatus,
            boolean expectedSuccess
    ) {
        Response response = auth.login(loginData);

        response.then()
                .statusCode(expectedStatus)
                .body("success", is(expectedSuccess));
    }

    // ------------------- Forgot Password -------------------
    @ParameterizedTest(name = "TC004 / TC009 / TC011 - Forgot Password Test Case [{index}]")
    @MethodSource("com.capstone.api_tests.testdata.AuthTestData#forgotPasswordTestCases")
    @Severity(SeverityLevel.NORMAL)
    public void testForgotPasswordParameterized(
            java.util.Map<String, Object> forgotPasswordData,
            int expectedStatus
    ) {
        Response response = auth.forgotPassword(forgotPasswordData);

        response.then()
                .statusCode(expectedStatus);
    }

    // ------------------- Logout -------------------
    @Test
    @Story("TC005 - Logout the current user")
    @Severity(SeverityLevel.NORMAL)
    public void testLogoutSuccessfully() {
        String token = TokenManager.getToken();
        Response response = auth.logout(token);

        response.then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Story("TC006 - Logout without token")
    @Severity(SeverityLevel.CRITICAL)
    public void testLogoutWithoutToken() {
        Response response = auth.logoutWithoutToken();

        response.then()
                .statusCode(anyOf(is(401), is(400)));
    }
}
