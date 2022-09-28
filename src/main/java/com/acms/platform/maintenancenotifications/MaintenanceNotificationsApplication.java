package com.acms.platform.maintenancenotifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.acms.platform", "com.alliance.platform"})
@EnableCaching
public class MaintenanceNotificationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaintenanceNotificationsApplication.class, args);
    }

}
