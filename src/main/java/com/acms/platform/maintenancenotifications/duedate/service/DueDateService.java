package com.acms.platform.maintenancenotifications.duedate.service;

import com.acms.platform.maintenancenotifications.duedate.model.MaintenanceDueDateResponse;

import java.time.LocalDate;

public interface DueDateService {

    void saveDueDate(String vehicleUuid, LocalDate dueDate);

    MaintenanceDueDateResponse getDueDate(String vehicleUuid);
}
