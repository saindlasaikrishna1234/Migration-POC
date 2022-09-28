package com.acms.platform.maintenancenotifications.functional.cucumber.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RabbitTestingConfig {

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(17987);
        factory.setHost("localhost");
        factory.setPassword("guest");
        factory.setUsername("guest");
        return factory;
    }
}
