Feature: From feature A1

  @smoke
  Scenario: From A1 - Smoke - Scenario A1 - 1
    Then all value match conditions
      | param | operation    | value |
      | 1     | contains     | 1     |
      | 2     | not equals      | 2     |
      | 3     | not contains | 4     |

#
#  @smoke
#  Scenario: From A1 - Smoke - Scenario A1 - 2
#    Given I open the browser
#    When I navigate to "https://example.com"
#    Then the title should be "Example Domain"
