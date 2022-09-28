package com.acms.platform.maintenancenotifications.duedate.controller;

import com.acms.platform.maintenancenotifications.duedate.model.MaintenanceDueDateRequest;
import com.acms.platform.maintenancenotifications.duedate.model.MaintenanceDueDateResponse;
import com.acms.platform.maintenancenotifications.duedate.service.DueDateService;
import com.aic.framework.vehicleidentity.VehicleIdentities;
import com.aic.framework.vehicleidentity.web.VehiclePathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Validated
public class DueDateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DueDateController.class);

    private final DueDateService dueDateService;

    @Autowired
    public DueDateController(DueDateService dueDateService) {
        this.dueDateService = dueDateService;
    }

    @PreAuthorize("isUser(#userId) and hasVin(#vehicleIdentities.getUuid()) and hasService(#vehicleIdentities.getUuid(), 101)")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/users/{userId}/vehicles/{vehicleId}/duedate")
    public void postMaintenanceDueDate(@VehiclePathVariable(name = "vehicleId") VehicleIdentities vehicleIdentities,
                                       @PathVariable("userId") String userId,
                                       @Valid @RequestBody MaintenanceDueDateRequest dueDateDto) {

        LOGGER.info("Post vehicleId {} with dueDate {}", vehicleIdentities.getUuid(), dueDateDto.getDueDate().toString());

        dueDateService.saveDueDate(vehicleIdentities.getUuid(), dueDateDto.getDueDate());
    }


    @PreAuthorize("isUser(#userId) and hasVin(#vehicleIdentities.getUuid()) and hasService(#vehicleIdentities.getUuid(), 101)")
    @GetMapping(value = "/users/{userId}/vehicles/{vehicleId}/duedate")
    public ResponseEntity<MaintenanceDueDateResponse> getMaintenanceDueDate(@VehiclePathVariable(name = "vehicleId") VehicleIdentities vehicleIdentities,
                                                                           @PathVariable("userId") String userId) {
        LOGGER.info("Get dueDate with vehicleId {}", vehicleIdentities.getUuid());

        MaintenanceDueDateResponse dto = dueDateService.getDueDate(vehicleIdentities.getUuid());
        return ResponseEntity.ok(dto);
    }
}
