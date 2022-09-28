package com.acms.platform.maintenancenotifications.duedate.service;

import com.acms.platform.maintenancenotifications.common.exception.ErrorCodeException;
import com.acms.platform.maintenancenotifications.duedate.model.MaintenanceDueDateResponse;
import com.acms.platform.maintenancenotifications.repository.VehicleParamRepository;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import com.acms.platform.maintenancenotifications.resetcycle.ResetMaintenanceCycleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DueDateServiceImplTest {
    public static final String VEHICLE_UUID = "e9adf97b-ba7d-4671-b56f-9fd5e56151ae";
    private static final LocalDate _01_01_2000 = LocalDate.of(2000, 1, 1);
    private static final LocalDate _01_01_2020 = LocalDate.of(2020, 1, 1);


    @InjectMocks
    private DueDateServiceImpl dueDateService;

    @Mock
    private VehicleParamRepository vehicleParamRepository;

    @Mock
    private ResetMaintenanceCycleService resetMaintenanceCycleService;

    @Captor
    ArgumentCaptor<VehicleParamEntity> vehicleParamEntityCaptor;


    @Test
    public void givenDueDateWithVehicleParam_whenCreateOrUpdateDueDate_thenShouldUpdate() {
        given(vehicleParamRepository.findByVehicleUuid(VEHICLE_UUID)).willReturn(new VehicleParamEntity().setId(2L));


        dueDateService.saveDueDate(VEHICLE_UUID, _01_01_2020);


        verify(vehicleParamRepository).save(vehicleParamEntityCaptor.capture());
        assertEquals(_01_01_2020, vehicleParamEntityCaptor.getValue().getDueDate());
        assertEquals(2L, vehicleParamEntityCaptor.getValue().getId());
    }

    @Test
    public void givenDueDateWithNewVehicleParam_whenCreateOrUpdateDueDate_thenShouldCreate() {
        VehicleParamEntity vehicleParamEntity = new VehicleParamEntity().setDueDate(_01_01_2020);
        vehicleParamEntity.setVehicleUuid(VEHICLE_UUID);
        given(vehicleParamRepository.findByVehicleUuid(VEHICLE_UUID)).willReturn(null);


        dueDateService.saveDueDate(VEHICLE_UUID, _01_01_2020);


        verify(vehicleParamRepository).save(vehicleParamEntity);
        verify(resetMaintenanceCycleService).resetNotificationHistory(VEHICLE_UUID);
    }

    @Test
    public void givenEntityExists_whenGetDueDate_thenShouldRetrieve() {
        VehicleParamEntity vehicleParamEntity = new VehicleParamEntity().setVehicleUuid(VEHICLE_UUID).setDueDate(_01_01_2000);
        given(vehicleParamRepository.findByVehicleUuid(VEHICLE_UUID)).willReturn(vehicleParamEntity);


        MaintenanceDueDateResponse dto = dueDateService.getDueDate(VEHICLE_UUID);

        verify(vehicleParamRepository).findByVehicleUuid(eq(VEHICLE_UUID));

        assertEquals(_01_01_2000, dto.getDueDate());
        assertEquals(VEHICLE_UUID, dto.getVehicleUuid());
    }

    @Test
    public void givenEntityDoesNotExist_whenGetDueDate_thenShouldThrowsError() {
        given(vehicleParamRepository.findByVehicleUuid(VEHICLE_UUID)).willReturn(null);


        ErrorCodeException e = assertThrows(ErrorCodeException.class, () -> dueDateService.getDueDate(VEHICLE_UUID));

        verify(vehicleParamRepository).findByVehicleUuid(eq(VEHICLE_UUID));
        assertEquals("ERROR 404002 : The due date has not been set for this vehicle: VehicleParamEntity - null.", e.getLogErrorMessage());
    }
}
