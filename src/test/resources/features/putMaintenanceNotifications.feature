Feature: Put Maintenance Notifications Alerts sent to user

  Scenario: 1- With maintenance alert request and notification pre-applicability conditions are not met i want to get decision to deny launch maintenance alert.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="4500"
    Given Current mileage with currentMileage="82500"
    Given First get is available for alert maintenance notification

    Given Receive alert maintenance notification is sent with success

    When get is available for alert maintenance notification with success
    Then return decision to deny launch maintenance alert

  Scenario: 2- With maintenance alert request and notification pre-applicability conditions are met and idNotificationRange is empty i want to get error response with Bad Request HTTP Status.
    Given System token with token="22222222-2222-2222-2222-222222222222"
    Given Vehicle Identification Number with vehicleUuid="e9adf97b-ba7d-4671-b56f-9fd5e56151ae"
    Given Remaining mileage with remainingMileage="4500"
    Given Current mileage with currentMileage="82500"
    Given First get is available for alert maintenance notification

    When Receive alert maintenance notification is sent with notificationIdRange is empty

    Then return Bad Request HTTP Status for Request Body