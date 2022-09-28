package com.acms.platform.maintenancenotifications.repository;

import com.acms.platform.maintenancenotifications.repository.entities.NotificationRangeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRangeRepository extends CrudRepository<NotificationRangeEntity, Long> {

    NotificationRangeEntity findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(int differenceInDays);

    NotificationRangeEntity findTopByOrderByIdDesc();

    NotificationRangeEntity findFirstByRangeGreaterThanEqualOrderByRangeAsc(long remainingMileage);
}
