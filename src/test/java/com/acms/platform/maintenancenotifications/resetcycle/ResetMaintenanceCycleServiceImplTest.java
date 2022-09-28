package com.acms.platform.maintenancenotifications.resetcycle;

import com.acms.platform.maintenancenotifications.repository.NotificationVehicleRepository;
import com.acms.platform.maintenancenotifications.repository.VehicleParamRepository;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(MockitoExtension.class)
class ResetMaintenanceCycleServiceImplTest {

    private static final String VEHICLE_UUID = "e9adf97b-ba7d-4671-b56f-9fd5e56151ae";
    private static final Long REMAINING_MILEAGE = 2000L;
    private static final Long RESET_REMAINING_MILEAGE = 15000L;
    private static final Long NOT_RESET_REMAINING_MILEAGE = 1913L;
    private static final Long REMAINING_MILEAGE_SLIGHTLY_GREATER = 2030L;
    private static final Long REMAINING_MILEAGE_GREATER_THAN_THRESHOLD = 2110L;

    private ResetMaintenanceCycleServiceImpl resetMaintenanceCycleService;

    @Mock
    private NotificationVehicleRepository notificationVehicleRepository;

    @Mock
    private VehicleParamRepository vehicleParamRepository;

    @BeforeEach
    void setUp() {
        resetMaintenanceCycleService = new ResetMaintenanceCycleServiceImpl(notificationVehicleRepository,
                vehicleParamRepository,
                "100");
    }

    @Test
    public void givenVehicleUuid_resetNotificationHistory_shouldCallRepository() {
        resetMaintenanceCycleService.resetNotificationHistory(VEHICLE_UUID);


        verify(notificationVehicleRepository).deleteByVehicleUuid(VEHICLE_UUID);
    }

    @Test
    public void givenNoVehicleParam_hasTheVehicleBeenServiced_shouldReturnFalse() {
        boolean result = resetMaintenanceCycleService.hasTheVehicleBeenServiced(null, REMAINING_MILEAGE);


        assertThat(result).isFalse();
    }

    @Test
    public void givenRemainingMileageNotReset_hasTheVehicleBeenServiced_shouldReturnFalse() {
        boolean result = resetMaintenanceCycleService.hasTheVehicleBeenServiced(buildVehicleParamEntity(), NOT_RESET_REMAINING_MILEAGE);


        assertThat(result).isFalse();
    }

    @Test
    public void givenRemainingMileageReset_hasTheVehicleBeenServiced_shouldReturnFalse() {
        boolean result = resetMaintenanceCycleService.hasTheVehicleBeenServiced(buildVehicleParamEntity(), RESET_REMAINING_MILEAGE);


        assertThat(result).isTrue();
    }

    @Test
    public void givenRemainingMileageEquals_hasTheVehicleBeenServiced_shouldReturnFalse() {
        boolean result = resetMaintenanceCycleService.hasTheVehicleBeenServiced(buildVehicleParamEntity(), REMAINING_MILEAGE);


        assertThat(result).isFalse();
    }

    @Test
    public void givenRemainingMileageSlightlyGreater_hasTheVehicleBeenServiced_shouldReturnFalse() {
        boolean result = resetMaintenanceCycleService.hasTheVehicleBeenServiced(buildVehicleParamEntity(), REMAINING_MILEAGE_SLIGHTLY_GREATER);


        assertThat(result).isFalse();
    }
    
    @Test
    public void givenRemainingMileageGreaterThanThreshold_hasTheVehicleBeenServiced_shouldReturnTrue() {
        boolean result = resetMaintenanceCycleService.hasTheVehicleBeenServiced(buildVehicleParamEntity(), REMAINING_MILEAGE_GREATER_THAN_THRESHOLD);
        assertThat(result).isTrue();
    }

    @Test
    public void givenNoVehicleParam_resetVehicleParam_shouldReset() {
        resetMaintenanceCycleService.resetVehicleParam(VEHICLE_UUID, null);


        verifyZeroInteractions(vehicleParamRepository);
    }

    @Test
    public void givenExistingVehicleParam_resetVehicleParam_shouldReset() {
        resetMaintenanceCycleService.resetVehicleParam(VEHICLE_UUID, buildVehicleParamEntity());


        verify(vehicleParamRepository).save(buildVehicleParamEntityWithoutDueDate());
    }

    private VehicleParamEntity buildVehicleParamEntity() {
        return new VehicleParamEntity()
                .setVehicleUuid(VEHICLE_UUID)
                .setRemainingMileage(REMAINING_MILEAGE)
                .setDueDate(LocalDate.now());
    }

    private VehicleParamEntity buildVehicleParamEntityWithoutDueDate() {
        return new VehicleParamEntity()
                .setVehicleUuid(VEHICLE_UUID);
    }
}
