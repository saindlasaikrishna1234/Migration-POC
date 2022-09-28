package com.acms.platform.maintenancenotifications.maintenancenotification.service;

import com.acms.platform.maintenancenotifications.common.utils.DateProvider;
import com.acms.platform.maintenancenotifications.repository.NotificationRangeRepository;
import com.acms.platform.maintenancenotifications.repository.NotificationVehicleRepository;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationRangeEntity;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationVehicleEntity;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RangeServiceImplTest {

    private static final String VEHICLE_UUID = "e9adf97b-ba7d-4671-b56f-9fd5e56151ae";
    private static final String DUE_DATE_RULE_KEY = "maintenance.due.date";
    private static final String DUE_MILEAGE_RULE_KEY = "maintenance.due.mileage";
    private static final String FINAL_RULE_KEY = "maintenance.due.final";
    private static final long REMAINING_MILEAGE_OUT = 8000L;
    private static final long REMAINING_MILEAGE_FIRST_RANGE = 4500L;
    private static final long REMAINING_MILEAGE_SECOND_RANGE = 2000L;
    private static final long REMAINING_MILEAGE_THIRD_RANGE = 1300L;
    private static final long REMAINING_MILEAGE_FOURTH_RANGE = 800L;
    private static final long REMAINING_MILEAGE_FIFTH_RANGE = 300L;
    private static final long REMAINING_MILEAGE_SIXTH_RANGE = 0L;
    private static final int FINAL_RANGE_ID = 6;
    private static final int HIGHER_RANGE_ID = 5;
    private static final int LOWER_RANGE_ID = 1;
    private static final LocalDate DUE_DATE = LocalDate.of(2020, 5, 1);

    private NotificationRangeEntity finalRange = buildRange(FINAL_RANGE_ID);

    @InjectMocks
    private RangeServiceImpl rangeService;
    @Mock
    private DateProvider dateProvider;
    @Mock
    private NotificationRangeRepository rangeRepository;
    @Mock
    private NotificationVehicleRepository notificationVehicleRepository;

    @BeforeEach
    public void before() {
        when(rangeRepository.findTopByOrderByIdDesc()).thenReturn(finalRange);
    }

    @Test
    public void givenNoRulesFound_retrieveConcernedRange_thenReturnEmptyPair() {
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT)).thenReturn(null);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, null, REMAINING_MILEAGE_OUT);


        assertThat(result).isEqualTo(Pair.of(null, null));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT);
        verifyZeroInteractions(notificationVehicleRepository);
    }

    @Test
    public void givenNoDueDateSavedAndOneMileageAndNotSent_retrieveConcernedRange_thenReturnMileageRange() {
        NotificationRangeEntity rangeFound = buildRange(LOWER_RANGE_ID);
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_FIRST_RANGE)).thenReturn(rangeFound);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, rangeFound)).thenReturn(buildSentVehicle());


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, null, REMAINING_MILEAGE_FIRST_RANGE);


        assertThat(result).isEqualTo(Pair.of(null, null));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_FIRST_RANGE);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, rangeFound);
    }

    @Test
    public void givenNoDueDateSavedAndOneMileageAndSent_retrieveConcernedRange_thenReturnNull() {
        NotificationRangeEntity rangeFound = buildRange(LOWER_RANGE_ID);
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_FIRST_RANGE)).thenReturn(rangeFound);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, rangeFound)).thenReturn(buildNotSentVehicle());


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, null, REMAINING_MILEAGE_FIRST_RANGE);


        assertThat(result).isEqualTo(Pair.of(DUE_MILEAGE_RULE_KEY, rangeFound));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_FIRST_RANGE);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, rangeFound);
    }

    @Test
    public void givenDueDateFoundAndNoMileageAndNotSent_retrieveConcernedRange_thenReturnDateRange() {
        NotificationRangeEntity rangeFound = buildRange(LOWER_RANGE_ID);
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE.plusDays(5));
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT)).thenReturn(null);
        when(rangeRepository.findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5)).thenReturn(rangeFound);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, rangeFound)).thenReturn(buildNotSentVehicle());
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, vehicle, REMAINING_MILEAGE_OUT);


        assertThat(result).isEqualTo(Pair.of(DUE_DATE_RULE_KEY, rangeFound));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT);
        verify(rangeRepository).findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, rangeFound);
    }

    @Test
    public void givenNegativeDateDifferenceAndNoMileageAndNotSent_retrieveConcernedRange_thenReturnFinalNotification() {
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE.minusDays(5));
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT)).thenReturn(null);
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, vehicle, REMAINING_MILEAGE_OUT);


        assertThat(result).isEqualTo(Pair.of(FINAL_RULE_KEY, finalRange));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, finalRange);
    }

    @Test
    public void givenFinalMileageAndNotSent_retrieveConcernedRange_thenReturnFinalNotification() {
        NotificationRangeEntity mileageRangeFound = finalRange;
        NotificationRangeEntity dateRangeFound = buildRange(LOWER_RANGE_ID);
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE.plusDays(5));
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT)).thenReturn(mileageRangeFound);
        when(rangeRepository.findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5)).thenReturn(dateRangeFound);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, mileageRangeFound)).thenReturn(buildNotSentVehicle());
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, dateRangeFound)).thenReturn(buildNotSentVehicle());
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, vehicle, REMAINING_MILEAGE_OUT);


        assertThat(result).isEqualTo(Pair.of(FINAL_RULE_KEY, mileageRangeFound));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT);
        verify(rangeRepository).findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, mileageRangeFound);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, dateRangeFound);
    }

    @Test
    public void givenDueDateFoundAndNoMileageAndSent_retrieveConcernedRange_thenReturnNull() {
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE);
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT)).thenReturn(null);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, finalRange)).thenReturn(buildSentVehicle());
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, vehicle, REMAINING_MILEAGE_OUT);


        assertThat(result).isEqualTo(Pair.of(null, null));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, finalRange);
    }

    @Test
    public void givenDueDateFoundAndMileageHigher_retrieveConcernedRange_thenReturnMileageRange() {
        NotificationRangeEntity mileageRangeFound = buildRange(HIGHER_RANGE_ID);
        NotificationRangeEntity dateRangeFound = buildRange(LOWER_RANGE_ID);
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE.plusDays(5));
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT)).thenReturn(mileageRangeFound);
        when(rangeRepository.findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5)).thenReturn(dateRangeFound);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, mileageRangeFound)).thenReturn(buildNotSentVehicle());
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, dateRangeFound)).thenReturn(buildNotSentVehicle());
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, vehicle, REMAINING_MILEAGE_OUT);


        assertThat(result).isEqualTo(Pair.of(DUE_MILEAGE_RULE_KEY, mileageRangeFound));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT);
        verify(rangeRepository).findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, mileageRangeFound);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, dateRangeFound);
    }

    @Test
    public void givenDueDateFoundHigherAndMileageLower_retrieveConcernedRange_thenReturnDateRange() {
        NotificationRangeEntity mileageRangeFound = buildRange(LOWER_RANGE_ID);
        NotificationRangeEntity dateRangeFound = buildRange(HIGHER_RANGE_ID);
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE.plusDays(5));
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT)).thenReturn(mileageRangeFound);
        when(rangeRepository.findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5)).thenReturn(dateRangeFound);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, mileageRangeFound)).thenReturn(buildNotSentVehicle());
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, dateRangeFound)).thenReturn(buildNotSentVehicle());
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, vehicle, REMAINING_MILEAGE_OUT);


        assertThat(result).isEqualTo(Pair.of(DUE_DATE_RULE_KEY, dateRangeFound));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT);
        verify(rangeRepository).findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, mileageRangeFound);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, dateRangeFound);
    }

    @Test
    public void givenDueDateFoundAndMileageLowerWithSamePriority_retrieveConcernedRange_thenReturnMileageRange() {
        NotificationRangeEntity mileageRangeFound = buildRange(HIGHER_RANGE_ID);
        NotificationRangeEntity dateRangeFound = buildRange(HIGHER_RANGE_ID);
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE.plusDays(5));
        when(rangeRepository.findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT)).thenReturn(mileageRangeFound);
        when(rangeRepository.findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5)).thenReturn(dateRangeFound);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, mileageRangeFound)).thenReturn(buildNotSentVehicle());
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, dateRangeFound)).thenReturn(buildNotSentVehicle());
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(VEHICLE_UUID, vehicle, REMAINING_MILEAGE_OUT);


        assertThat(result).isEqualTo(Pair.of(DUE_MILEAGE_RULE_KEY, mileageRangeFound));
        verify(rangeRepository).findFirstByRangeGreaterThanEqualOrderByRangeAsc(REMAINING_MILEAGE_OUT);
        verify(rangeRepository).findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, mileageRangeFound);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, dateRangeFound);
    }

    @Test
    public void givenEVNoDateFound_retrieveConcernedRange_thenReturnEmptyPair() {
        VehicleParamEntity vehicle = buildVehicle(null);

        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(vehicle);

        assertThat(result).isEqualTo(Pair.of(null, null));
        verifyZeroInteractions(rangeRepository);
        verifyZeroInteractions(notificationVehicleRepository);
    }

    @Test
    public void givenEVDueDateFoundAndNotSent_retrieveConcernedRange_thenReturnDateRange() {
        NotificationRangeEntity rangeFound = buildRange(LOWER_RANGE_ID);
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE.plusDays(5));
        when(rangeRepository.findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5)).thenReturn(rangeFound);
        when(notificationVehicleRepository.findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, rangeFound)).thenReturn(buildNotSentVehicle());
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(vehicle);


        assertThat(result).isEqualTo(Pair.of(DUE_DATE_RULE_KEY, rangeFound));
        verify(rangeRepository).findFirstByRemainingTimeInDaysGreaterThanEqualOrderByRemainingTimeInDaysAsc(5);
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, rangeFound);
    }

    @Test
    public void givenEVNegativeDateDifferenceAndNotSent_retrieveConcernedRange_thenReturnFinalNotification() {
        VehicleParamEntity vehicle = buildVehicle(DUE_DATE.minusDays(5));
        given(dateProvider.getNow()).willReturn(DUE_DATE);


        Pair<String, NotificationRangeEntity> result = rangeService.retrieveConcernedRange(vehicle);


        assertThat(result).isEqualTo(Pair.of(DUE_DATE_RULE_KEY, finalRange));
        verify(notificationVehicleRepository).findByVehicleUuidAndNotificationRangeEntity(VEHICLE_UUID, finalRange);
    }

    private NotificationVehicleEntity buildNotSentVehicle() {
        return new NotificationVehicleEntity().setSend(false);
    }

    private NotificationVehicleEntity buildSentVehicle() {
        return new NotificationVehicleEntity().setSend(true);
    }


    private NotificationRangeEntity buildRange(int id) {
        return new NotificationRangeEntity()
                .setId(id)
                .setRange(23L)
                .setRemainingTime(12);
    }

    private VehicleParamEntity buildVehicle(LocalDate date) {
        return new VehicleParamEntity()
                .setDueDate(date)
                .setVehicleUuid(VEHICLE_UUID);
    }
}
