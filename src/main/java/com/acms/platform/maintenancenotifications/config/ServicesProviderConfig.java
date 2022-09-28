package com.acms.platform.maintenancenotifications.config;

import com.acms.platform.common.security.kauth.ServicesProvider;
import com.acms.platform.websecurity.services.ServicesProviderImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServicesProviderConfig {

    @Bean
    public ServicesProvider servicesProvider(@Qualifier("kamereonInternalRestTemplate") RestTemplate restTemplate, @Value("${services.provider.path}") String servicesProviderPath) {
        return new ServicesProviderImpl(restTemplate, servicesProviderPath);
    }
}
