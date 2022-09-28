package com.acms.platform.maintenancenotifications.duedate.service;

import com.acms.platform.maintenancenotifications.duedate.model.MaintenanceDueDateResponse;
import com.acms.platform.maintenancenotifications.repository.VehicleParamRepository;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import com.acms.platform.maintenancenotifications.resetcycle.ResetMaintenanceCycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

import static com.acms.platform.maintenancenotifications.common.exception.ErrorCodeException.buildErrorCodeException;
import static com.acms.platform.maintenancenotifications.common.exception.errorcodes.ClientErrorCode.NO_ENTITY_FOUND;

@Service
public class DueDateServiceImpl implements DueDateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DueDateServiceImpl.class);

    private final VehicleParamRepository vehicleParamRepository;
    private final ResetMaintenanceCycleService resetMaintenanceCycleService;

    @Autowired
    public DueDateServiceImpl(VehicleParamRepository vehicleParamRepository, ResetMaintenanceCycleService resetMaintenanceCycleService) {
        this.vehicleParamRepository = vehicleParamRepository;
        this.resetMaintenanceCycleService = resetMaintenanceCycleService;
    }

    @Override
    public void saveDueDate(String vehicleUuid, LocalDate dueDate) {
        VehicleParamEntity vehicleParam = getVehicleParamEntity(vehicleUuid, dueDate);
        vehicleParam.setDueDate(dueDate);
        vehicleParamRepository.save(vehicleParam);
        LOGGER.info("Set due date for vehicleUuid : {}, dueDate : {}", vehicleParam.getVehicleUuid(), vehicleParam.getDueDate());

        resetMaintenanceCycleService.resetNotificationHistory(vehicleUuid);
    }

    private VehicleParamEntity getVehicleParamEntity(String vehicleUuid, LocalDate dueDate) {
        VehicleParamEntity vehicleParam = vehicleParamRepository.findByVehicleUuid(vehicleUuid);
        if (vehicleParam == null) {
            return new VehicleParamEntity()
                    .setVehicleUuid(vehicleUuid)
                    .setDueDate(dueDate);
        }
        return vehicleParam;
    }

    @Override
    public MaintenanceDueDateResponse getDueDate(String vehicleUuid) {
        VehicleParamEntity vehicleParamEntity = vehicleParamRepository.findByVehicleUuid(vehicleUuid);
        if (Objects.isNull(vehicleParamEntity)) {
            throw buildErrorCodeException(NO_ENTITY_FOUND, "VehicleParamEntity", "null");
        }
        return mapMaintenanceDueDateDto(vehicleParamEntity);
    }

    private MaintenanceDueDateResponse mapMaintenanceDueDateDto(VehicleParamEntity vehicleParamEntity) {
        return new MaintenanceDueDateResponse()
                .setVehicleUuid(vehicleParamEntity.getVehicleUuid())
                .setDueDate(vehicleParamEntity.getDueDate());
    }
}
