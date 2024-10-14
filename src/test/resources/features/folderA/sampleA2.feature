Feature: From feature A2

  @smoke2
  Scenario: From A2 - WithoutSmoke - Scenario A2 - 1
    Given I open the browser
    When open url "https://example.com" in browser
    Given Page "Example_Page" is loaded
    Then field "h1_Text" is equal "Example Domain"