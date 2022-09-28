package com.acms.platform.maintenancenotifications.duedate.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class MaintenanceDueDateResponse {
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate dueDate;
    private String vehicleUuid;

    public LocalDate getDueDate() {
        return dueDate;
    }

    public MaintenanceDueDateResponse setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public String getVehicleUuid() {
        return vehicleUuid;
    }

    public MaintenanceDueDateResponse setVehicleUuid(String vehicleUuid) {
        this.vehicleUuid = vehicleUuid;
        return this;
    }
}
