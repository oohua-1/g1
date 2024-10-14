Feature: From feature A1

  @smoke
  Scenario: From A1 - Smoke - Scenario A1 - 1
    Given restful web service "postObjects" is created with domain "%{utils.env('dev_endpoint')}%"
    When restful headers are set with value
      | params       | value            |
      | Content-type | application-json |
    When restful body are set with values
      | params | value                |
      | name   | Apple MacBook Pro 20 |
      | model  | Intel Core i9        |
    When restful request is sent
    Then restful response value at "$.name" is saved to variable "a"
    Then track the following variables before operation
      | variableName | value |
      | nameBefore   | %{a}% |
      | tax          | 10    |

    Given restful web service "postObjects" is created with domain "%{utils.env('dev_endpoint')}%"
    When restful headers are set with value
      | params       | value            |
      | Content-type | application-json |
    When restful body are set with values
      | params | value  |
      | name   | Iphone |
      | model  | Intel  |
    When restful request is sent
    Then restful response value at "$.name" is saved to variable "b"

    Then track the following variables after operation
      | variableName | value |
      | nameAfter    | %{b}% |
      | tax          | 20    |

    Then print all tracked variables
