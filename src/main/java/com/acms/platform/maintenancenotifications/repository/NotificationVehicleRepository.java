package com.acms.platform.maintenancenotifications.repository;

import com.acms.platform.maintenancenotifications.repository.entities.NotificationRangeEntity;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationVehicleEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface NotificationVehicleRepository extends CrudRepository<NotificationVehicleEntity, Long> {

    NotificationVehicleEntity findByVehicleUuidAndNotificationRangeEntity(String vehicleUuid, NotificationRangeEntity notificationRangeEntity);

    List<NotificationVehicleEntity> findByVehicleUuid(String vehicleUuid);

    @Transactional
    @Modifying
    @Query(value = "SELECT notifVehicle FROM NotificationVehicleEntity notifVehicle " +
            " WHERE notifVehicle.creationDate < :limitOfRetention")
    List<NotificationVehicleEntity> findOldData(@Param("limitOfRetention") OffsetDateTime limitOfRetention);


    int deleteByVehicleUuid(String vehicleUuid);
}
