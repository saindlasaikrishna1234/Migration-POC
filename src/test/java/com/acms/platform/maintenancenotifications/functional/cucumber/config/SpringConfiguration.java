package com.acms.platform.maintenancenotifications.functional.cucumber.config;

import com.acms.platform.maintenancenotifications.functional.cucumber.utils.WireMockUtils;
import com.acms.platform.maintenancenotifications.repository.NotificationRangeRepository;
import com.acms.platform.maintenancenotifications.repository.NotificationVehicleRepository;
import com.acms.platform.maintenancenotifications.repository.VehicleParamRepository;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationRangeEntity;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.acms.platform.maintenancenotifications.functional.cucumber.config.BaseFunctionalTest.KAUTH_TOKEN_INFO;
import static com.acms.platform.maintenancenotifications.functional.cucumber.config.BaseFunctionalTest.SYSTEM_ACCESS_TOKEN_WITH_SCOPES;
import static com.acms.platform.maintenancenotifications.functional.cucumber.utils.WireMockUtils.getResourceAsString;
import static com.acms.platform.maintenancenotifications.functional.cucumber.utils.WireMockUtils.stubPostUrl;


@CucumberContextConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = {"classpath:application-test.properties"})
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Import({DockerConfig.class, RabbitTestingConfig.class})
public class SpringConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfiguration.class);
    protected static final String TOKEN_USER = "22222222-2222-2222-2222-000000000000";
    protected static final String TOKEN_USER_NO_SERVICES = "66666666-2222-2222-2222-000000000000";
    private static final String KAUTH_ACCESS_TOKEN = "/kauth/oauth2/access_token";

    @Value(value = "classpath:test_data/token/payload_access_support.json")
    protected Resource token_with_support_scopes_content;
    @Value(value = "classpath:test_data/token/token_user.json")
    private Resource tokenUserInfo;
    @Value(value = "classpath:test_data/token/token_user_no_services.json")
    private Resource tokenUserInfoNoServices;

    @Autowired
    private NotificationVehicleRepository notificationVehicleRepository;
    @Autowired
    private VehicleParamRepository vehicleParamRepository;

    @Before
    public void setUp() {
        WireMockUtils.stubGetUrl(KAUTH_TOKEN_INFO + SYSTEM_ACCESS_TOKEN_WITH_SCOPES,
                getResourceAsString(token_with_support_scopes_content));
        WireMockUtils.stubGetUrl(KAUTH_TOKEN_INFO + TOKEN_USER, getResourceAsString(tokenUserInfo));
        WireMockUtils.stubGetUrl(KAUTH_TOKEN_INFO + TOKEN_USER_NO_SERVICES, getResourceAsString(tokenUserInfoNoServices));
        stubPostUrl(KAUTH_ACCESS_TOKEN, "{\n" +
                "  \"access_token\": \"fake_275d-8de3-4092-91f0-429329e902fd\",\n" +
                "  \"token_type\": \"Bearer\",\n" +
                "  \"expires_in\": 3599\n" +
                "}");
        LOGGER.info("The Spring Configuration is initialized");
    }

    @After
    public void tearDown() {
        LOGGER.info("The Spring Configuration is going to be destroyed");

        vehicleParamRepository.deleteAll();
        notificationVehicleRepository.deleteAll();
    }

}
