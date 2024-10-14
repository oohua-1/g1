Feature: From feature A1

  @smoke
  Scenario: From A1 - Smoke - Scenario A1 - 1
    When perform SQL query to database "LocalDB"
      | query                                            |
      | SELECT * FROM Employees WHERE name = 'John Doe'; |

    When perform SQL query to database "LocalDB"
      | query                                            |
      | SELECT * FROM Employees WHERE Department = 'IT'; |

    Then the latest result from database "LocalDB" is saved to variable
      | column | rowIndex | variable   |
      | Name   | 1        | name_var   |
      | Salary | 2        | salary_var |

    Then the following values from database "LocalDB" should match
      | column  | rowIndex | operation | value      |
      | Name    | 1        | equals    | Jane Smith |
      | Salary  | 2        | contains  | 75000      |

    When wait up to 300 seconds, polling every 30 seconds, for SQL query to return "STATUS" with value "ACTIVE" in database "LocalDB"
      | query                                            |
      | SELECT * FROM Employees WHERE name = 'John Doe'; |
