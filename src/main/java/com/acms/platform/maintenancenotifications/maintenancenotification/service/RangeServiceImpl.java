package com.acms.platform.maintenancenotifications.maintenancenotification.service;

import com.acms.platform.maintenancenotifications.common.utils.DateProvider;
import com.acms.platform.maintenancenotifications.repository.NotificationRangeRepository;
import com.acms.platform.maintenancenotifications.repository.NotificationVehicleRepository;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationRangeEntity;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationVehicleEntity;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class RangeServiceImpl implements RangeService {

    private static final String DUE_DATE_RULE_KEY = "maintenance.due.date";
    private static final String DUE_MILEAGE_RULE_KEY = "maintenance.due.mileage";
    private static final String FINAL_RULE_KEY = "maintenance.due.final";

    private final NotificationRangeRepository rangeRepository;
    private final DateProvider dateProvider;
    private final NotificationVehicleRepository notificationVehicleRepository;

    public RangeServiceImpl(NotificationRangeRepository rangeRepository,
                            DateProvider dateProvider,
                            NotificationVehicleRepository notificationVehicleRepository) {
        this.rangeRepository = rangeRepository;
        this.dateProvider = dateProvider;
        this.notificationVehicleRepository = notificationVehicleRepository;
    }

    @Override
    public Pair<String, NotificationRangeEntity> retrieveConcernedRange(String vehicleUuid, VehicleParamEntity vehicle, long currentMileage) {
        NotificationRangeEntity dateRange = retrieveDateRange(vehicle);
        NotificationRangeEntity mileageRange = retrieveMileageRange(vehicleUuid, currentMileage);
        NotificationRangeEntity finalRange = rangeRepository.findTopByOrderByIdDesc();
        if (dateRange == null && mileageRange == null) return Pair.of(null, null);
        if (finalRange.equals(mileageRange) || finalRange.equals(dateRange)) return Pair.of(FINAL_RULE_KEY, finalRange);
        if (dateRange == null) return Pair.of(DUE_MILEAGE_RULE_KEY, mileageRange);
        if (mileageRange == null) return Pair.of(DUE_DATE_RULE_KEY, dateRange);

        return dateRange.getId() <= mileageRange.getId()
                ? Pair.of(DUE_MILEAGE_RULE_KEY, mileageRange)
                : Pair.of(DUE_DATE_RULE_KEY, dateRange);
    }
    
    @Override
    public Pair<String, NotificationRangeEntity> retrieveConcernedRange(VehicleParamEntity vehicle) {
        NotificationRangeEntity dateRange = retrieveDateRange(vehicle);
        if (dateRange == null) return Pair.of(null, null);

        return Pair.of(DUE_DATE_RULE_KEY, dateRange);
    }

    private NotificationRangeEntity retrieveDateRange(VehicleParamEntity vehicle) {
        if (vehicle == null || vehicle.getDueDate() == null) {
            return null;
        }

        long daysBetween = DAYS.between(dateProvider.getNow(), vehicle.getDueDate());
        NotificationRangeEntity dateRange = null;
        if (daysBetween <= 0) {
            NotificationRangeEntity finalRange = rangeRepository.findTopByOrderByIdDesc();
            dateRange = finalRange;
        } else {
            dateRange = rangeRepository.findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc((int) daysBetween);
        }
        return isNotificationSent(vehicle.getVehicleUuid(), dateRange) ? null : dateRange;
    }

    private boolean isNotificationSent(String vehicleId, NotificationRangeEntity range) {
        NotificationVehicleEntity notificationVehicleEntity = notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(vehicleId, range);
        if (Objects.isNull(notificationVehicleEntity)) {
            return false;
        }
        return notificationVehicleEntity.getSend();
    }

    private NotificationRangeEntity retrieveMileageRange(String vehicleUuid, long remainingMileage) {
        NotificationRangeEntity mileageRange = rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(remainingMileage);

        if (mileageRange == null) {
            return null;
        }
        return isNotificationSent(vehicleUuid, mileageRange) ? null : mileageRange;
    }
}
