Feature: From feature A1
#  Feature Description: This is a description for feature.

  @smoke
# Scenario description: This is a description for scenario
  Scenario: From A1 - Smoke - Scenario A1 - 1
#    Then value "%{utils.env('price')}%" equals "3"
    When the following values are saved to variables
      | value | variable |
      | 1     | example1     |
      | 2    | example2    |
      | 3    | example3     |
    Then value "%{example1}%" equals "3"
    Then value "%{example2}%" equals "10"
