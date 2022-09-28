Feature: Purge history for data retention

  Scenario: Purge history on scheduler message received
    Given we persist 1 param to be purged and 2 to keep
    And we persist 1 notification to be purged and 2 to keep
    When the scheduler notification is sent
    Then the application should have purged data when scheduled

  Scenario: Purge user history on delete user vehicle link event
    Given we persist 1 param to be purged and 2 to keep
    And we persist 1 notification to be purged and 2 to keep
    When the user vehicle link delete event is sent
    Then the application should have purged data