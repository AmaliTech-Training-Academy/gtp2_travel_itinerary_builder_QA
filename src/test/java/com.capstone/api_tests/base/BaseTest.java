package com.capstone.api_tests.base;

import com.capstone.api_tests.utils.TokenManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.lessThan;

public class BaseTest {

    public static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;
    protected String token;

    @BeforeAll
    public static void setUp() {

        RestAssured.baseURI = System.getProperty("baseUrl", "https://21j0kdg4g2.execute-api.eu-west-1.amazonaws.com/staging/api/v1");
        RestAssured.filters(new AllureRestAssured());

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectHeader("Content-Type", containsString("application/json"))
                .expectResponseTime(lessThan(5000L))
                .build();
    }

    @BeforeEach
    public void initToken() {
        token = TokenManager.getToken(); // fresh token per test
    }
}
