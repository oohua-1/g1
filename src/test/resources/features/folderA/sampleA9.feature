Feature: From feature A1

  @smoke
  Scenario: From A1 - Smoke - Scenario A1 - 1
    Given I open the browser
    When open url "https://practicetestautomation.com/practice-test-login/" in browser
    Given Page "Login_Page" is loaded
    Then validate negative case
      | case | actions                                                                                                 | validations                                                     |
      |------|---------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|
      | 1    | userName_Input[do: clear -> fill(username)], password_Input[do: fill(password1)], submit_Button[do: click] | error_Text[do: contains(Your username is invalid!)]             |
      | 2    | userName_Input[do: clear -> fill(username)], password_Input[do: fill(12)], submit_Button[do: click]     | error_Text[do: contains(Your username is invalid!)]           |
    Then field "userName_Input" is filled with value "student"
    Then field "password_Input" is filled with value "Password123"
    Then field "submit_Button" is clicked
    Given Page "SuccessFull_Page" is loaded



#
#  @smoke
#  Scenario: From A1 - Smoke - Scenario A1 - 2
#    Given I open the browser
#    When I navigate to "https://example.com"
#    Then the title should be "Example Domain"
