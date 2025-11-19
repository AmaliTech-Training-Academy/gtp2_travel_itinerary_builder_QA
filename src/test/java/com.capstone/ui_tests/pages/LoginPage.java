package com.capstone.pages;

import com.capstone.ui_tests.base.BasePage;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class LoginPage extends BasePage {


    // Page Elements
    private final SelenideElement emailInput = $("[formcontrolname='email']");
    private final SelenideElement passwordInput = $("[formcontrolname='password']");
    private final SelenideElement loginButton = $$(".btn.btn--primary").findBy(Condition.text("Log In"));
    private final SelenideElement errorMessage = $(".error-message");
    private final SelenideElement homeLoginButton = $(".nav__auth-link");
    private final SelenideElement profileIcon = $("svg path[d='M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2']");
    private final SelenideElement toastMessage = $(".toast-message");



    public void openLoginModal() {
        click(homeLoginButton);
    }


    // Page Actions
    public void login(String email, String password) {
        openLoginModal();
        type(emailInput, email);
        type(passwordInput, password);
        click(loginButton);
    }


    public String getErrorMessage() {
        if (isVisible(errorMessage)) {
            return getText(errorMessage);
        }
        return "";
    }


    public void openLoginPage(String url) {
        openPage(url);
    }

    public void verifyLoginSuccess() {
        profileIcon.shouldBe(Condition.visible);
    }

    public String getToastMessage() {
        // Wait until the toast appears (visible) and capture its text
        return toastMessage.shouldBe(Condition.visible, Duration.ofSeconds(2)).getText();
    }
}
