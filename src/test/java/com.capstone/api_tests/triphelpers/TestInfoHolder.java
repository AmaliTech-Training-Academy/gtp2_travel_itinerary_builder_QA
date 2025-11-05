package com.capstone.api_tests.triphelpers;

public class TestInfoHolder {

    public static boolean isCreateTripTest() {
        return Thread.currentThread()
                .getStackTrace()[3]
                .getMethodName()
                .contains("testCreateTrip");
    }
}
