package com.acms.platform.maintenancenotifications.config;


import com.aic.framework.vehicleidentity.VehicleIdentityAutoConfiguration;
import com.aic.framework.vehicleidentity.enpoint.VehicleIdentityRestTemplateProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@AutoConfigureBefore({VehicleIdentityAutoConfiguration.class})
public class CustomVehicleIdentityAutoConfiguration {

    @Bean
    public VehicleIdentityRestTemplateProvider vehicleIdentityRestTemplateProvider(
            @Qualifier("kamereonInternalRestTemplate") RestTemplate authenticatedRestTemplate) {
        return () -> authenticatedRestTemplate;
    }
}
