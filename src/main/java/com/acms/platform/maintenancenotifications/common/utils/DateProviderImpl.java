package com.acms.platform.maintenancenotifications.common.utils;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("!test")
public class DateProviderImpl implements DateProvider {
    @Override
    public LocalDate getNow() {
        return LocalDate.now();
    }
}
