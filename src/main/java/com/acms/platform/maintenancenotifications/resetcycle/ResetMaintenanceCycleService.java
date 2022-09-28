package com.acms.platform.maintenancenotifications.resetcycle;

import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;

public interface ResetMaintenanceCycleService {

    void resetNotificationHistory(String vehicleUuid);

    boolean hasTheVehicleBeenServiced(VehicleParamEntity vehicleParamEntity, long currentRemainingMileage);

    void resetVehicleParam(String vehicleUuid, VehicleParamEntity vehicleParamEntity);
}
