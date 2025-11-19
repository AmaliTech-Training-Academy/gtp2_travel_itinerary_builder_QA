package com.capstone.ui_tests.stepdefinitions;

import com.capstone.pages.LoginPage;
import com.capstone.ui_tests.util.TestDataLoader;
import com.capstone.ui_tests.util.User;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

    private final LoginPage loginPage = new LoginPage();
    private String toastText;

    @Given("I am on the homepage")
    public void iAmOnTheHomepage() {
        loginPage.openLoginPage("/");
    }

    // --- VALID LOGIN ---
    @When("I login with valid credentials")
    public void iLoginWithValidCredentials() {
        User validUser = TestDataLoader.getUser("valid_login");
        loginPage.login(validUser.getEmail(), validUser.getPassword());
    }

    // --- LOGIN USING USER KEY FROM JSON ---
    @When("I login as {string}")
    public void iLoginAs(String userKey) {
        User user = TestDataLoader.getUser(userKey);
        loginPage.login(user.getEmail(), user.getPassword());
        toastText = loginPage.getToastMessage();
    }

    // --- RAW STRING LOGIN (optional) ---
    @When("I login with email {string} and password {string}")
    public void iLoginWithEmailAndPassword(String email, String password) {
        loginPage.login(email, password);
        toastText = loginPage.getToastMessage();
    }

    // --- SUCCESS VALIDATION ---
    @Then("I should see my profile icon")
    public void iShouldSeeMyProfileIcon() {
        loginPage.verifyLoginSuccess();
    }

    // --- JSON-BASED ERROR VALIDATION ---
    @Then("I should see error {string}")
    public void iShouldSeeError(String errorKey) {
        String expectedError = TestDataLoader.getErrorMessage(errorKey);
        assertTrue(toastText.contains(expectedError),
                "❌ Expected error message was not displayed");
    }
}
