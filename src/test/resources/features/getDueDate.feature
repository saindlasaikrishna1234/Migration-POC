Feature: Maintenance alert V1 - handling the maintenance due date

  Scenario: 1- New end-point to get the maintenance due date
    Given User id ="123456"
    And System token with token ="22222222-2222-2222-2222-000000000000"
    And Vehicle Identification Number with vehicleUuid ="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    And Vehicle services returns service 101
    And a vehicle where the maintenance due date was already set
    When the get due date is called
    Then the latest due date is returned
    And check vehicle identity is called

  Scenario: 1.5 - New end-point to get the maintenance due date with vin
    Given User id ="123456"
    And Vehicle Identification Number with vehicleUuid ="e9adf97b-ba7d-4671-b56f-9fd5e56151ae" stub with encrypted vin
    And System token with token ="22222222-2222-2222-2222-000000000000"
    And Vehicle services returns service 101
    And a vehicle where the maintenance due date was already set
    When the get due date is called with vin ="VF1RFD00755818671"
    Then the latest due date is returned
    And check vehicle identity is called with encrypted vin

  Scenario: 2- Get maintenance due date return 404002 error when no due date was saved for this vehicle
    Given User id ="123456"
    And Vehicle Identification Number with vehicleUuid ="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    And System token with token ="22222222-2222-2222-2222-000000000000"
    And Vehicle services returns service 101
    And a vehicle where the maintenance due date was never set
    When the get due date is called
    Then a 404002 error is returned
    And check vehicle identity is called

  Scenario: 3- Get maintenance due date with service not activated should return a formatted 403 exception
    Given User id ="123456"
    And Vehicle Identification Number with vehicleUuid ="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    And System token with token ="66666666-2222-2222-2222-000000000000"
    And Vehicle services returns no service
    And a vehicle where the maintenance due date was already set
    When the get due date is called
    Then a 403000 error is returned
    And check vehicle identity is called