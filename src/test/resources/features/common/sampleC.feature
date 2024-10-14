Feature: From feature A1
  @smoke
  Scenario: From A1 - Smoke - Scenario A1 - 1
    When the following values are saved to variables
      | value | variable |
      | 1     | example1     |
      | 2    | example2    |
      | 3    | example3     |
    Then value "%{example1}%" equals "3"
    Then value "%{example2}%" equals "10"
