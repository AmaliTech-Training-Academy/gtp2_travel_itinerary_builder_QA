package com.capstone.ui_tests.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.*;

public abstract class BaseTest {

    @BeforeEach
    void setUp() {
        Configuration.baseUrl = "https://d3uvhar7i2kujw.cloudfront.net";
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
        Configuration.browser = "chrome";
        Configuration.browserSize = null;
        Configuration.timeout = 10000;

        open("/");

        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        sleep(1000);
        closeWebDriver();
    }
}
