package com.acms.platform.maintenancenotifications.functional.cucumber.stepdefs;

import com.acms.platform.maintenancenotifications.functional.cucumber.utils.AmqpSender;
import com.acms.platform.maintenancenotifications.functional.cucumber.utils.KmnChecker;
import com.acms.platform.maintenancenotifications.functional.cucumber.utils.Variable;
import com.acms.platform.maintenancenotifications.repository.NotificationVehicleRepository;
import com.acms.platform.maintenancenotifications.repository.VehicleParamRepository;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationVehicleEntity;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.acms.platform.maintenancenotifications.functional.cucumber.config.BaseFunctionalTest.getResourceAsString;
import static org.assertj.core.api.Assertions.assertThat;

public class DataRetentionStepDefs {

    private static final OffsetDateTime TIMESTAMP = OffsetDateTime.now();
    private static final String VEHICLE_UUID = "9f4e86a7-5e4f-44c6-b01a-79d2abd5348a";
    private static final String OTHER_VEHICLE_UUID = "613f3e7e-3b07-11eb-adc1-0242ac120002";
    private final String deleteLinkBindingKey = "kam.user.delete.uservehicle.a-ncb";

    @Value("${scheduler.exchange}")
    private String schedulerExchange;
    @Value("${scheduler.dataretention.bindingkey}")
    private String schedulerBindingKey;
    @Value("${users.exchange}")
    private String usersExchange;

    private final NotificationVehicleRepository vehicleRepository;
    private final VehicleParamRepository vehicleParamRepository;
    private final AmqpSender amqpSender;

    @Value(value = "classpath:test_data/dataretention/delete_user_vehicle_link_message.json")
    private Resource deleteLinkMessage;
    @Value(value = "classpath:test_data/dataretention/scheduler_message.json")
    private Resource schedulerMessage;

    public DataRetentionStepDefs(NotificationVehicleRepository vehicleRepository, VehicleParamRepository vehicleParamRepository, AmqpSender amqpSender) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleParamRepository = vehicleParamRepository;
        this.amqpSender = amqpSender;
    }

    @Given("we persist 1 notification to be purged and 2 to keep")
    public void saveOldNotification() {
        List<NotificationVehicleEntity> vehicleEntities = new ArrayList<>();
        vehicleEntities.add(new NotificationVehicleEntity().setVehicleUuid(OTHER_VEHICLE_UUID).setCreationDate(TIMESTAMP));
        vehicleEntities.add(new NotificationVehicleEntity().setVehicleUuid(OTHER_VEHICLE_UUID).setCreationDate(TIMESTAMP.minusDays(89)));
        vehicleEntities.add(new NotificationVehicleEntity().setVehicleUuid(VEHICLE_UUID).setCreationDate(TIMESTAMP.minusDays(92)));
        vehicleRepository.saveAll(vehicleEntities);
    }

    @Given("we persist 1 param to be purged and 2 to keep")
    public void saveParam() {
        List<VehicleParamEntity> vehicleParamEntities = new ArrayList<>();
        vehicleParamEntities.add(new VehicleParamEntity().setVehicleUuid(OTHER_VEHICLE_UUID).setCreationDate(TIMESTAMP));
        vehicleParamEntities.add(new VehicleParamEntity().setVehicleUuid(OTHER_VEHICLE_UUID).setCreationDate(TIMESTAMP.minusDays(89)));
        vehicleParamRepository.saveAll(vehicleParamEntities);
        VehicleParamEntity entity = vehicleParamRepository.save(new VehicleParamEntity().setVehicleUuid(VEHICLE_UUID));
        entity.setCreationDate(TIMESTAMP.minusDays(92));
        vehicleParamRepository.save(entity);
    }

    @When("the scheduler notification is sent")
    public void the_scheduler_notification_is_sent() {
        amqpSender.send(getResourceAsString(schedulerMessage), schedulerExchange, schedulerBindingKey, Collections.emptyMap(), new Variable("TIMESTAMP", TIMESTAMP.toString()));
    }

    @When("the user vehicle link delete event is sent")
    public void the_user_vehicle_link_delete_event_is_sent() {
        amqpSender.send(getResourceAsString(deleteLinkMessage), usersExchange, deleteLinkBindingKey, Collections.emptyMap(), new Variable("TIMESTAMP", TIMESTAMP.toString()));
    }

    @Then("the application should have purged data")
    public void purge_data() {
        KmnChecker.checkWithRetry(() -> {
            List<NotificationVehicleEntity> vehicleEntities = (List<NotificationVehicleEntity>) vehicleRepository.findAll();
            assertThat(vehicleEntities.size()).isEqualTo(2);
            List<VehicleParamEntity> vehicleParamEntities = (List<VehicleParamEntity>) vehicleParamRepository.findAll();
            assertThat(vehicleParamEntities.size()).isEqualTo(2);
        });
    }

    @Then("the application should have purged data when scheduled")
    public void purge_data_when_schedule() {
        KmnChecker.checkWithRetry(() -> {
            List<NotificationVehicleEntity> vehicleEntities = (List<NotificationVehicleEntity>) vehicleRepository.findAll();
            assertThat(vehicleEntities.size()).isEqualTo(2);
        });
    }

}