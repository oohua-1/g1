Feature: From feature A1

  @smoke
  Scenario: From A1 - Smoke - Scenario A1 - 1
    Given I open the browser
    When open url "https://www.saucedemo.com/" in browser
    Given Page "LoginSauceLab_Page" is loaded
    Then field "h1_Text" is equal "Swag Labs"
    Then field "user_Input" is cleared by keyboard
    Then field "password_Input" is cleared by keyboard
    Then field "user_Input" is filled with value "standard_user"
    Then field "password_Input" is filled with value "secret_sauce"
    Then field "login_Button" is clicked
