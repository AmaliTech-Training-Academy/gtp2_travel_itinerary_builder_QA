package com.capstone.api_tests.testdata;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class AuthTestData {

    /** ✅ Registration (unique email every run) */
    public static Map<String, Object> validRegistrationDataWithWrongOTP() {
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "Nana");
        data.put("lastName", "Quaci");
        data.put("email", "collins.qa+" + System.currentTimeMillis() + "@example.com");
        data.put("password", "Password123!");
        data.put("otp", "123967");
        return data;
    }

    public static Stream<Arguments> validRegistrationDataWithWrongOTPStream() {
        return Stream.of(
                Arguments.of("Nana", "Quaci", "collins.qa+" + System.currentTimeMillis() + "@example.com", "Password123!", "123967"),
                Arguments.of("Nana", "Quaci", "collins.qa+" + System.currentTimeMillis() + "@example.com", "Password123!", "000000"),
                Arguments.of("Nana", "Quaci", "collins.qa+" + System.currentTimeMillis() + "@example.com", "Password123!", "")
        );
    }

    /** Login test data */
    public static Map<String, Object> validLoginData() {
        Map<String, Object> data = new HashMap<>();
        data.put("email", "collins.adu@amalitechtraining.org");
        data.put("password", "SecurePass123!");
        return data;
    }

    public static Map<String, Object> invalidLoginData() {
        Map<String, Object> data = new HashMap<>();
        data.put("email", "wrong@example.com");
        data.put("password", "wrongPass123!");
        return data;
    }

    public static Map<String, Object> wrongPasswordLoginData() {
        Map<String, Object> data = new HashMap<>();
        data.put("email", "collins.qa@test.com");
        data.put("password", "WrongPassword123!");
        return data;
    }

    public static Map<String, Object> nonExistentUserLoginData() {
        Map<String, Object> data = new HashMap<>();
        data.put("email", "nouser" + System.currentTimeMillis() + "@example.com");
        data.put("password", "Password123!");
        return data;
    }

    public static Map<String, Object> emptyLoginData() {
        return new HashMap<>();
    }

    /** Forgot password test data */
    public static Map<String, Object> forgotPasswordData() {
        Map<String, Object> data = new HashMap<>();
        data.put("email", "collins.qa@test.com");
        return data;
    }

    public static Map<String, Object> unregisteredEmailData() {
        Map<String, Object> data = new HashMap<>();
        data.put("email", "unregistered" + System.currentTimeMillis() + "@example.com");
        return data;
    }

    public static Map<String, Object> emptyForgotPasswordData() {
        return new HashMap<>();
    }

    /** ✅ Parameterized login provider */
    public static Stream<Arguments> loginTestCases() {
        return Stream.of(
                Arguments.of(validLoginData(), 200, true),                   // valid login
                Arguments.of(invalidLoginData(), 401, false),                // invalid email
                Arguments.of(wrongPasswordLoginData(), 401, false),          // wrong password
                Arguments.of(nonExistentUserLoginData(), 401, false),        // non-existent user
                Arguments.of(emptyLoginData(), 400, false)                   // missing fields
        );
    }

    /** ✅ Parameterized forgot password provider */
    public static Stream<Arguments> forgotPasswordTestCases() {
        return Stream.of(
                Arguments.of(forgotPasswordData(), 200),                     // valid email
                Arguments.of(unregisteredEmailData(), 404),                  // unregistered email
                Arguments.of(emptyForgotPasswordData(), 400)                 // missing email
        );
    }

    /** ✅ Reset Password Placeholder */
    public static Map<String, Object> resetPasswordData() {
        Map<String, Object> data = new HashMap<>();
        data.put("token", "dummy-reset-token");
        data.put("newPassword", "NewPassword123!");
        return data;
    }
}
