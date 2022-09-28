package com.acms.platform.maintenancenotifications.duedate.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class MaintenanceDueDateRequest {
    @JsonFormat(pattern = "yyyyMMdd")
    @NotNull
    private LocalDate dueDate;

    public LocalDate getDueDate() {
        return dueDate;
    }

    public MaintenanceDueDateRequest setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }
}
