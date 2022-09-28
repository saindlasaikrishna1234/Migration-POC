package com.acms.platform.maintenancenotifications.maintenancenotification.service;

import com.acms.platform.maintenancenotifications.repository.entities.NotificationRangeEntity;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.apache.commons.lang3.tuple.Pair;

public interface RangeService {
    Pair<String, NotificationRangeEntity> retrieveConcernedRange(String vehicleUuid, VehicleParamEntity vehicle, long currentMileage);
    
    Pair<String, NotificationRangeEntity> retrieveConcernedRange(VehicleParamEntity vehicle);
}
