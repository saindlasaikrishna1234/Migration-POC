package com.acms.platform.maintenancenotifications.dataretention.service;

import java.time.OffsetDateTime;

public interface DataRetentionService {
    void purgeData(OffsetDateTime timestamp, int retentionDays);

    void deleteUserVehicleHistory(String vehicleUuid);
}
