package com.acms.platform.maintenancenotifications.maintenancenotification.service;

import com.acms.platform.maintenancenotifications.maintenancenotification.model.Metadata;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;

import java.util.List;

public interface MetadataProvider {
    List<Metadata> buildMetadata(VehicleParamEntity vehicle);
}
