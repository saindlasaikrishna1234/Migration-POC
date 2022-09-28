package com.acms.platform.maintenancenotifications.maintenancenotification.service;

import com.acms.platform.maintenancenotifications.common.utils.DateProvider;
import com.acms.platform.maintenancenotifications.maintenancenotification.model.Metadata;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class MetadataProviderImpl implements MetadataProvider {

    private static final String REMAINING_TIME_KEY = "day.number";
    private static final String POST_MAINTENANCE_DUE_DATE_NOT_CALLED_VALUE = "undefined";

    private final DateProvider dateProvider;

    public MetadataProviderImpl(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    @Override
    public List<Metadata> buildMetadata(VehicleParamEntity vehicle) {
        List<Metadata> metadata = new ArrayList<>();
        String daysBetween = vehicle == null || vehicle.getDueDate() == null
                ? POST_MAINTENANCE_DUE_DATE_NOT_CALLED_VALUE
                : String.valueOf(DAYS.between(dateProvider.getNow(), vehicle.getDueDate()));
        metadata.add(new Metadata(REMAINING_TIME_KEY, daysBetween));
        return metadata;
    }
}
