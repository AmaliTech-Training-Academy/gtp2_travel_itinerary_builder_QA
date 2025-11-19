Feature: Login functionality

  Scenario: Valid login
    Given I am on the homepage
    When I login as "valid_login"
    Then I should see my profile icon

  Scenario Outline: Invalid login scenarios
    Given I am on the homepage
    When I login as "<userKey>"
    Then I should see error "<errorKey>"

    Examples:
      | userKey          | errorKey           |
      | invalid_login    | invalidcredentials |
      | empty_email      | emptyFields        |
      | empty_password   | emptyFields        |
      | empty_credentials| emptyFields        |
