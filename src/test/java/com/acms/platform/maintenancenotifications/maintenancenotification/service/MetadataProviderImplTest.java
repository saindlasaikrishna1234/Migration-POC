package com.acms.platform.maintenancenotifications.maintenancenotification.service;

import com.acms.platform.maintenancenotifications.common.utils.DateProvider;
import com.acms.platform.maintenancenotifications.maintenancenotification.model.Metadata;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetadataProviderImplTest {

    private static final int REMAINING_TIME = 5;
    private static final String UNDEFINED = "undefined";

    @InjectMocks
    public MetadataProviderImpl metadataProvider;
    @Mock
    private DateProvider dateProvider;

    @Test
    void givenNoVehicle_buildMetadata_shouldBeSetToUndefined() {
        List<Metadata> metadata = metadataProvider.buildMetadata(null);


        assertThat(metadata.size()).isEqualTo(1);
        assertThat(metadata.get(0)).isEqualTo(buildMetadata(UNDEFINED));
    }

    @Test
    void givenDueDateHasBeenSet_buildMetadata_shouldBeSetToUndefined() {
        LocalDate now = LocalDate.now();
        when(dateProvider.getNow()).thenReturn(now);


        List<Metadata> metadata = metadataProvider.buildMetadata(buildVehicle(now));


        assertThat(metadata.size()).isEqualTo(1);
        assertThat(metadata.get(0)).isEqualTo(buildMetadata(REMAINING_TIME));
    }

    private VehicleParamEntity buildVehicle(LocalDate now) {
        return new VehicleParamEntity().setDueDate(now.plusDays(REMAINING_TIME));
    }

    private Metadata buildMetadata(int remainingTime) {
        return buildMetadata(String.valueOf(remainingTime));
    }

    private Metadata buildMetadata(String remainingTime) {
        return new Metadata("day.number", remainingTime);
    }
}