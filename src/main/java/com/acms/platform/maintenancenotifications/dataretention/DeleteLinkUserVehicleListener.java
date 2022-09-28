package com.acms.platform.maintenancenotifications.dataretention;

import com.acms.platform.maintenancenotifications.dataretention.model.DeleteUserVehicleLinkMessage;
import com.acms.platform.maintenancenotifications.dataretention.service.DataRetentionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DeleteLinkUserVehicleListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteLinkUserVehicleListener.class);


    private final ObjectMapper objectMapper;
    private final DataRetentionService dataRetentionService;

    public DeleteLinkUserVehicleListener(@Qualifier("amqpObjectMapper") ObjectMapper objectMapper,
                         DataRetentionService dataRetentionService) {
        this.objectMapper = objectMapper;
        this.dataRetentionService = dataRetentionService;
    }

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = "${users.deletelinkuservehicle.queue}", durable = "true", arguments = {
                    @Argument(
                            name = "x-message-ttl",
                            value = "${rmq.ttl.in.millis}",
                            type = "java.lang.Integer"
                    )
            }),
            exchange = @Exchange(value = "${users.exchange}", type = "topic"),
            key = "${users.deletelinkuservehicle.bindingkey}")}
    )
    public void handleDeleteUserVehicleLinkMessage(Message message) {
        String consumerQueue = message.getMessageProperties().getConsumerQueue();
        LOGGER.info("Message has been received in {} queue: '{}'", consumerQueue, new String(message.getBody()));

        DeleteUserVehicleLinkMessage deleteLinkMessage;
        try {
            deleteLinkMessage = objectMapper.readValue(message.getBody(), DeleteUserVehicleLinkMessage.class);
        } catch (IOException e) {
            LOGGER.error("Can't deserialize received message: " + message.toString());
            return;
        }

        dataRetentionService.deleteUserVehicleHistory(deleteLinkMessage.getData().getVehicleUuid());
    }
}
