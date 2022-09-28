Feature: Get Maintenance Notifications User context

  Scenario: 1- With maintenance alert request and notification pre-applicability conditions are met i want to get decision to launch maintenance alert.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="4500"
    Given Current mileage with currentMileage="82500"
    Given Remaining time is="undefined"
    When get is available for alert maintenance notification with success
    Then return decision to launch maintenance alert
    Then The remaining mileage has been saved

  Scenario: 2- With maintenance alert request and notification pre-applicability conditions are not met i want to get decision to deny launch maintenance alert.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="4500"
    Given Current mileage with currentMileage="82500"
    Given Remaining time is="undefined"
    Given First get is available for alert maintenance notification
    When get is available for alert maintenance notification with success
    Then return decision to launch maintenance alert
    Then The remaining mileage has been saved

  Scenario: 3- With maintenance alert request and remaining mileage is missing in notification pre-applicability conditions i want to get error response with Bad Request HTTP Status.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage=""
    Given Current mileage with currentMileage="82500"
    Given Remaining time is="undefined"
    When get is available for alert maintenance notification
    Then return Bad Request HTTP Status for remaining mileage

  Scenario: 4- With maintenance alert request and current mileage is missing in notification pre-applicability conditions i want to get error response with Bad Request HTTP Status.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="4500"
    Given Current mileage with currentMileage=""
    Given Remaining time is="undefined"
    When get is available for alert maintenance notification
    Then return Bad Request HTTP Status for current mileage

  Scenario: 5- Maintenance alert nominal case due date pre-notification
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="4500"
    Given Current mileage with currentMileage="82500"
    Given the due date is nowadays
    When get is available for alert maintenance notification with success
    Then the notification is sent to the user
    Then The remaining mileage has been saved

  Scenario: 6- Maintenance alert nominal case due date pre-notification with the notification applicability rule is met twice (due date & due mileage)
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="400"
    Given Current mileage with currentMileage="82500"
    Given the due date is nowadays
    Given Notification alerts have been sent previously
    When get is available for alert maintenance notification with success
    Then the higher notification is sent to the user
    Then The notification alert histories have not been erased
    Then The remaining mileage has been saved

  Scenario: 7- Maintenance alert nominal case final notification with the notification applicability rule is met
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="5000"
    Given Current mileage with currentMileage="0"
    Given the notification applicability rule is met due date
    When get is available for alert maintenance notification with success
    Then the notification is sent to the user with rangeId="3" and ruleKey="maintenance.due.date"
    Then The remaining mileage has been saved

  Scenario: 8- Maintenance alert with second call for same range
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="5000"
    Given Current mileage with currentMileage="4000"
    Given Remaining time is="undefined"
    Given the notification is not send but already saved
    When get is available for alert maintenance notification with success
    Then the notification is sent to the user with rangeId="1" and ruleKey="maintenance.due.mileage"
    Then the notification vehicle range is not duplicated
    Then The remaining mileage has been saved

  Scenario: 9- Maintenance alert with due date in the past
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="8000"
    Given Current mileage with currentMileage="8000"
    Given the due date is in the past
    When get is available for alert maintenance notification with success
    Then the notification is sent to the user with rangeId="6" and ruleKey="maintenance.due.final"

  Scenario: 10- Maintenance alert with due date in the past already sent
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given the final notification is already sent
    Given Remaining mileage with remainingMileage="8000"
    Given Current mileage with currentMileage="8000"
    Given the due date is in the past
    When get is available for alert maintenance notification with success
    Then  return decision to deny launch maintenance alert

  Scenario: 11- Maintenance alert with alert on due date day
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="8000"
    Given Current mileage with currentMileage="8000"
    Given the due date is nowadays
    When get is available for alert maintenance notification with success
    Then the notification is sent to the user with rangeId="6" and ruleKey="maintenance.due.final"
    Then The remaining mileage has been saved

  Scenario: 12- With maintenance alert request and the remaining mileage has been reset, then the maintenance cycle is reset.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="15000"
    Given Current mileage with currentMileage="82500"
    Given the due date is not met
    Given Notification alerts have been sent previously
    When get is available for alert maintenance notification with success
    Then return decision to deny launch maintenance alert
    Then The notification alert histories have been erased
    Then The due date and remaining mileage have been erased

  Scenario: 13- With maintenance alert request with difference between old remaining mileage and current remaining mileage less than the threshold, then the maintenance cycle is not reset.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="4550"
    Given Current mileage with currentMileage="82500"
    Given the due date is not met
    Given Notification alerts have been sent previously
    When get is available for alert maintenance notification with success
    Then The notification alert histories have not been erased
    Then The due date and remaining mileage have not been erased

  Scenario: 14- With maintenance alert request with difference between old remaining mileage and current remaining mileage more than the threshold, then the maintenance cycle is reset.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="5550"
    Given Current mileage with currentMileage="82500"
    Given the due date is not met
    Given Notification alerts have been sent previously
    When get is available for alert maintenance notification with success
    Then The notification alert histories have been erased
    Then The due date and remaining mileage have been erased
