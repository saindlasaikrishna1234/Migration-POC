package com.acms.platform.maintenancenotifications.resetcycle;

import com.acms.platform.maintenancenotifications.repository.NotificationVehicleRepository;
import com.acms.platform.maintenancenotifications.repository.VehicleParamRepository;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ResetMaintenanceCycleServiceImpl implements ResetMaintenanceCycleService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ResetMaintenanceCycleServiceImpl.class);

    private final NotificationVehicleRepository notificationVehicleRepository;
    private final VehicleParamRepository vehicleParamRepository;

    private final String thresholdMileageValue;

    public ResetMaintenanceCycleServiceImpl(NotificationVehicleRepository notificationVehicleRepository,
                                            VehicleParamRepository vehicleParamRepository,
                                            @Value("${mileage.threshold}") String thresholdMileageValue) {
        this.notificationVehicleRepository = notificationVehicleRepository;
        this.vehicleParamRepository = vehicleParamRepository;
        this.thresholdMileageValue = thresholdMileageValue;
    }

    @Override
    @Transactional
    public void resetNotificationHistory(String vehicleUuid) {
        int entriesNumber = notificationVehicleRepository.deleteByVehicleUuid(vehicleUuid);
        LOGGER.info("Reset: {} notifications for maintenance history were removed for vehicleUuid : {}", entriesNumber, vehicleUuid);
    }

    @Override
    public boolean hasTheVehicleBeenServiced(VehicleParamEntity vehicleParamEntity, long currentRemainingMileage) {
        if (vehicleParamEntity == null) {
            return false;
        }

        Long oldRemainingMileage = vehicleParamEntity.getRemainingMileage();
        return oldRemainingMileage != null && currentRemainingMileage > (oldRemainingMileage + Long.parseLong(thresholdMileageValue));
    }

    @Override
    public void resetVehicleParam(String vehicleUuid, VehicleParamEntity vehicleParamEntity) {
        if (vehicleParamEntity == null) {
            return;
        }
        vehicleParamEntity.setDueDate(null);
        vehicleParamEntity.setRemainingMileage(null);
        vehicleParamRepository.save(vehicleParamEntity);
    }
}
