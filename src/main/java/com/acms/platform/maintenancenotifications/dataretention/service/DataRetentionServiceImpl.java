package com.acms.platform.maintenancenotifications.dataretention.service;

import com.acms.platform.maintenancenotifications.repository.NotificationVehicleRepository;
import com.acms.platform.maintenancenotifications.repository.VehicleParamRepository;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationVehicleEntity;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DataRetentionServiceImpl implements DataRetentionService {

    private final NotificationVehicleRepository vehicleRepository;

    private final VehicleParamRepository vehicleParamRepository;

    @Autowired
    public DataRetentionServiceImpl(NotificationVehicleRepository vehicleRepository, VehicleParamRepository vehicleParamRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleParamRepository = vehicleParamRepository;
    }


    @Override
    @Transactional
    public void purgeData(OffsetDateTime timestamp, int retentionDays) {
        OffsetDateTime limitOfRetention = timestamp.minusDays(retentionDays);
        vehicleRepository.deleteAll(vehicleRepository.findOldData(limitOfRetention));

    }

    @Override
    @Transactional
    public void deleteUserVehicleHistory(String vehicleUuid) {
        vehicleRepository.deleteAll(vehicleRepository.findByVehicleUuid(vehicleUuid));
        vehicleParamRepository.delete(vehicleParamRepository.findByVehicleUuid(vehicleUuid));
    }
}
