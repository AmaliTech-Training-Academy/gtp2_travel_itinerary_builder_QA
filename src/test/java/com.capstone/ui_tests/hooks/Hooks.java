package com.capstone.ui_tests.hooks;

import com.codeborne.selenide.WebDriverRunner;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import com.codeborne.selenide.Configuration;

import static com.codeborne.selenide.Selenide.*;

public class Hooks {

    @Before
    public void setUp() {
        Configuration.baseUrl = "https://d3uvhar7i2kujw.cloudfront.net";
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
        Configuration.timeout = 6000;
        Configuration.pageLoadTimeout = 15000;
        Configuration.browserSize = null;

        WebDriverRunner.clearBrowserCache();

        open("/");
        if (!Configuration.headless) {
            WebDriverRunner.getWebDriver().manage().window().maximize();
        }
    }

    @After
    public void tearDown() {
        closeWebDriver();
    }
}
