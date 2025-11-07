package com.capstone.api_tests.testdata;

import java.util.HashMap;
import java.util.Map;

public class TripTestData {

    public static final String VALID_EMAIL = "testmate@example.com";
    public static final String NON_EXIST_EMAIL = "nobody@example.com";
    public static final String INVALID_TRIP_ID ="1234-invalid";
    public static final String SAMPLE_TRIP_ID = "4ee8ee0b-f098-4821-953a-c23c12d9d238";
    public static final String SAMPLE_TRIP_INVITE_ID = "c8be0d12-b4e0-46e6-ad96-37e9d5836065";

    public static String uniqueEmail() {
        return "testuser_" + System.currentTimeMillis() + "@example.com";
    }


    public static Map<String, Object> validTripData(String destination) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "QA Trip " + System.currentTimeMillis());
        data.put("destination", destination);
        data.put("startDate", "2025-11-15");
        data.put("endDate", "2025-11-18");
        data.put("travelers", 2);
        return data;
    }

    public static Object[][] invalidTripPayloads() {
        return new Object[][]{
                {tripWithMissingDestination(), 400, "destination"},
                {tripMissingDates("Nairobi"), 422, "date"},
                {invalidDateTrip("Accra"), 422, "date"},
                {largeTravelerTrip("Accra"), 400, "travelers"}
        };
    }


    // Missing destination
    public static Map<String, Object> tripWithMissingDestination() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Trip Missing Destination " + System.currentTimeMillis());
        data.put("startDate", "2025-12-01");
        data.put("endDate", "2025-12-10");
        data.put("travelers", 1);
        return data;
    }

    // Missing dates
    public static Map<String, Object> tripMissingDates(String destination) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Trip Missing Dates " + System.currentTimeMillis());
        data.put("destination", destination);
        data.put("travelers", 1);
        return data;
    }

    // Invalid date range (end < start)
    public static Map<String, Object> invalidDateTrip(String destination) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Invalid Date Trip " + System.currentTimeMillis());
        data.put("destination", destination);
        data.put("startDate", "2025-12-04");
        data.put("endDate", "2025-12-15"); // end before start
        data.put("travelers", 2);
        return data;
    }

    // Update request (future API)
    public static Map<String, Object> updateTripData() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Updated QA Trip");
        data.put("travelers", 3);
        return data;
    }

    public static Map<String, Object> largeTravelerTrip(String destination) {
        var data = validTripData(destination);
        data.put("travelers", 9999);
        return data;
    }

}
