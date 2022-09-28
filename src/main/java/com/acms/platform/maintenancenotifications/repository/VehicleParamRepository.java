package com.acms.platform.maintenancenotifications.repository;

import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import java.util.List;

@Repository
public interface VehicleParamRepository extends CrudRepository<VehicleParamEntity, Long> {

    VehicleParamEntity findByVehicleUuid(String vehicleUuid);

}
