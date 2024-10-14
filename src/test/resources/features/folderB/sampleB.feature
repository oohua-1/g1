Feature: From feature B1

  @Smoke
  Scenario: From B1 - Smoke - Scenario B1 - 1
    Given I open the browser
    When open url "https://example.com" in browser
    Given Page "Example_Page" is loaded
    Then field "h1_Text" is equal "Example Domain"
