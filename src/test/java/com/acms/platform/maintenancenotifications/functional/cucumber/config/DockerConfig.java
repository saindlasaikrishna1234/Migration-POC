package com.acms.platform.maintenancenotifications.functional.cucumber.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@TestConfiguration
@Order(HIGHEST_PRECEDENCE)
public class DockerConfig {

    public static final DockerComposeContainer ENV ;
    private static final String COMPOSE_PATH = "src/test/resources/docker-compose.yml";

    static {
        ENV = new DockerComposeContainer(new File(COMPOSE_PATH)).withLocalCompose(true);
        ENV.start();
        ENV.waitingFor("rabbitmq", Wait.forHealthcheck());
    }
}
