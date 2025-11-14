import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const loginDuration = new Trend('login_duration');
const registerDuration = new Trend('register_duration');
const apiCallCounter = new Counter('api_calls');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 10 },  // Ramp up to 10 users
    { duration: '1m', target: 20 },   // Stay at 20 users
    { duration: '30s', target: 0 },   // Ramp down to 0
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% of requests should be below 2s
    http_req_failed: ['rate<0.1'],     // Error rate should be less than 10%
    errors: ['rate<0.1'],
    login_duration: ['p(95)<3000'],
    register_duration: ['p(95)<3000'],
  },
};

// Base URL
const BASE_URL = 'https://21j0kdg4g2.execute-api.eu-west-1.amazonaws.com/staging/api/v1';

// Test data generator
function generateRandomEmail() {
  const timestamp = Date.now();
  const random = Math.floor(Math.random() * 10000);
  return `testuser${timestamp}${random}@k6test.com`;
}

function generateRandomUser() {
  return {
    email: generateRandomEmail(),
    password: 'TestPass123!',
    firstName: 'Test',
    lastName: 'User'
  };
}

// Helper function to check response
function checkResponse(response, expectedStatus, metricName) {
  const success = check(response, {
    [`${metricName}: status is ${expectedStatus}`]: (r) => r.status === expectedStatus,
    [`${metricName}: response has body`]: (r) => r.body.length > 0,
    [`${metricName}: response is valid JSON`]: (r) => {
      try {
        JSON.parse(r.body);
        return true;
      } catch (e) {
        return false;
      }
    },
  });

  if (!success) {
    errorRate.add(1);
    console.error(`${metricName} failed:`, response.status, response.body);
  } else {
    errorRate.add(0);
  }

  apiCallCounter.add(1);
  return success;
}

// Test scenarios
export default function () {
  // Randomly choose a scenario to simulate real user behavior
  const scenarios = [
    testHealthCheck,
    testLoginFlow,
    testRegistrationFlow,
    testForgotPasswordFlow,
  ];

  const scenario = scenarios[Math.floor(Math.random() * scenarios.length)];
  scenario();

  sleep(1); // Think time between requests
}

// 1. Health Check / API Availability
function testHealthCheck() {
  const response = http.get(`${BASE_URL}/health`, {
    tags: { name: 'HealthCheck' },
  });

  checkResponse(response, 200, 'Health Check');
}

// 2. Login Flow Test
function testLoginFlow() {
  // Using a predefined test account (you should create this manually first)
  const loginPayload = JSON.stringify({
    email: 'aducollins48@gmail.com',
    password: 'GOODBOYs@123'
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
    tags: { name: 'Login' },
  };

  const startTime = Date.now();
  const response = http.post(`${BASE_URL}/auth/login`, loginPayload, params);
  const duration = Date.now() - startTime;

  loginDuration.add(duration);

  const success = checkResponse(response, 200, 'Login');

  if (success) {
    const body = JSON.parse(response.body);

    check(body, {
      'Login: has accessToken': (b) => b.data && b.data.accessToken,
      'Login: has user data': (b) => b.data && b.data.user,
      'Login: success is true': (b) => b.success === true,
    });

    // If login successful, test an authenticated endpoint
    if (body.data && body.data.accessToken) {
      testAuthenticatedEndpoint(body.data.accessToken);
    }
  }
}

// 3. Registration Flow Test (OTP + Register)
function testRegistrationFlow() {
  const user = generateRandomUser();

  // Step 1: Request OTP
  const otpResponse = http.post(
    `${BASE_URL}/auth/verify-email?email=${user.email}`,
    null,
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'RequestOTP' },
    }
  );

  const otpSuccess = checkResponse(otpResponse, 200, 'Request OTP');

  if (otpSuccess) {
    check(otpResponse, {
      'OTP: success message received': (r) => {
        const body = JSON.parse(r.body);
        return body.success === true;
      },
    });
  }

  sleep(1); // Simulate user delay

  // Note: In real scenario, we can't get the actual OTP without email access
  // This part would normally fail in automated tests
  // For load testing purposes, we're measuring the OTP request endpoint
}

// 4. Forgot Password Flow
function testForgotPasswordFlow() {
  const forgotPasswordPayload = JSON.stringify({
    email: 'testuser@example.com'
  });

  const response = http.post(
    `${BASE_URL}/auth/forgot-password`,
    forgotPasswordPayload,
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'ForgotPassword' },
    }
  );

  const success = checkResponse(response, 200, 'Forgot Password');

  if (success) {
    check(response, {
      'ForgotPassword: success message': (r) => {
        const body = JSON.parse(r.body);
        return body.success === true && body.message.includes('Email sent');
      },
    });
  }
}

// 5. Test Authenticated Endpoint (example)
function testAuthenticatedEndpoint(token) {
  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    tags: { name: 'AuthenticatedRequest' },
  };

  // Example: Get user profile or trips
  const response = http.get(`${BASE_URL}/trips`, params);

  check(response, {
    'Authenticated: status is 200 or 404': (r) => r.status === 200 || r.status === 404,
    'Authenticated: not unauthorized': (r) => r.status !== 401,
  });

  if (response.status === 401) {
    errorRate.add(1);
  } else {
    errorRate.add(0);
  }

  apiCallCounter.add(1);
}

// 6. OAuth Endpoint Check
export function testOAuthEndpoint() {
  const response = http.get(
    'https://21j0kdg4g2.execute-api.eu-west-1.amazonaws.com/staging/oauth2/authorization/google',
    {
      redirects: 0, // Don't follow redirects
      tags: { name: 'OAuthInitiation' },
    }
  );

  check(response, {
    'OAuth: redirects to Google': (r) => r.status === 302 || r.status === 307,
  });
}

// Setup function - runs once at the beginning
export function setup() {
  console.log('🚀 Starting K6 Performance Tests for Travel Itinerary Builder API');
  console.log(`📍 Base URL: ${BASE_URL}`);
  console.log('⏱️  Test duration: 2 minutes');
  console.log('👥 Max virtual users: 20');

  // Test if API is reachable
  const healthCheck = http.get(`${BASE_URL}/health`, {
    timeout: '10s',
  });

  if (healthCheck.status !== 200) {
    console.warn('⚠️  Warning: API health check failed. Proceeding with tests anyway...');
  } else {
    console.log('✅ API is reachable');
  }

  return { startTime: Date.now() };
}

// Teardown function - runs once at the end
export function teardown(data) {
  const duration = (Date.now() - data.startTime) / 1000;
  console.log(`\n🏁 Test completed in ${duration.toFixed(2)} seconds`);
  console.log('📊 Check the generated report for detailed metrics');
}