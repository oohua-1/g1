Feature: From feature A1
#  Feature Description: This is a description for feature.

  @smoke
# Scenario description: This is a description for scenario
  Scenario: From A1 - Smoke - Scenario A1 - 1

  @smoke
# Scenario description: This is a description for the conditional scenario
  Scenario: Conditional Step Example
    When value "6" is saved to variable "forC"
    Then value "3" equals "3"
    Then create new "Feature" from "common/sampleC" with
      | variable | value |
      | example1 | 3     |
      | example2 | 10    |
    When value "3" is saved to variable "example10"