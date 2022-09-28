Feature: Maintenance alert V1 - handling the maintenance due date

  Scenario: 1- Set the maintenance due date for a vehicle
    Given User id ="123456"
    Given System token with token ="22222222-2222-2222-2222-000000000000"
    Given Vehicle Identification Number with vehicleUuid ="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Vehicle services returns service 101
    Given a vehicle where the maintenance due date was never set
    When the end-point is called
    Then the maintenance due date is set for this vehicle

  Scenario: 2- Set the maintenance due date for a vehicle with an existing one
    Given User id ="123456"
    Given System token with token ="22222222-2222-2222-2222-000000000000"
    Given Vehicle Identification Number with vehicleUuid ="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Vehicle services returns service 101
    Given a vehicle where the maintenance due date was already set
    When the end-point is called
    Then the maintenance due date is modified for this vehicle

  Scenario: 3- The date body parameter is not set and kmn returns a formatted error 400002
    Given User id ="123456"
    Given System token with token ="22222222-2222-2222-2222-000000000000"
    Given Vehicle Identification Number with vehicleUuid ="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Vehicle services returns service 101
    Given a vehicle where the maintenance due date was already set
    When the end-point is called without the date
    Then the 400002 error is returned formatted

  Scenario: 4- Set the maintenance due date for a vehicle
    Given User id ="123456"
    Given System token with token ="22222222-2222-2222-2222-000000000000"
    Given Vehicle Identification Number with vehicleUuid ="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Vehicle services returns service 101
    Given a vehicle where the maintenance due date was never set
    Given a vehicle was sent some maintenance alert previously
    When the end-point is called
    Then the maintenance due date is set for this vehicle
    Then the previous maintenance alert histories are erased
