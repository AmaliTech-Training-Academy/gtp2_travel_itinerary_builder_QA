package com.capstone.ui_tests.tests;

import org.junit.jupiter.api.Test;
import com.capstone.ui_tests.base.BaseTest;
import com.capstone.pages.LoginPage;
import com.capstone.ui_tests.util.TestDataLoader;
import com.capstone.ui_tests.util.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseTest {
    @Test
    void validLoginTest() {
        User validUser = TestDataLoader.getUser("valid_login");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage("/");
        loginPage.login(validUser.getEmail(), validUser.getPassword());

        loginPage.verifyLoginSuccess();
    }


    @ParameterizedTest(name = "{0} should show error: {1}")
    @MethodSource("com.capstone.ui_tests.util.TestDataLoader#negativeLoginData")
    void negativeLoginTests(String userKey, String expectedErrorKey) {
        User user = TestDataLoader.getUser(userKey);
        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage("/");
        loginPage.login(user.getEmail(), user.getPassword());

        String expectedError = TestDataLoader.getErrorMessage(expectedErrorKey);
        assertTrue(loginPage.getToastMessage().contains(expectedError),
                "❌ Expected login error message was not displayed for " + userKey);
    }

}

