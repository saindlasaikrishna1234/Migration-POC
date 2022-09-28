package com.acms.platform.maintenancenotifications.dataretention;

import com.acms.platform.maintenancenotifications.dataretention.model.DataRetentionMessage;
import com.acms.platform.maintenancenotifications.dataretention.service.DataRetentionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SchedulerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerListener.class);

    private final ObjectMapper objectMapper;
    private final DataRetentionService dataRetentionService;

    @Autowired
    public SchedulerListener(@Qualifier("amqpObjectMapper") ObjectMapper objectMapper,
                             DataRetentionService dataRetentionService) {
        this.objectMapper = objectMapper;
        this.dataRetentionService = dataRetentionService;
    }

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = "${scheduler.dataretention.queue}", durable = "true", arguments = {
                    @Argument(
                            name = "x-message-ttl",
                            value = "${rmq.ttl.in.millis}",
                            type = "java.lang.Integer"
                    )
            }),
            exchange = @Exchange(value = "${scheduler.exchange}", type = "topic"),
            key = "${scheduler.dataretention.bindingkey}")}
    )
    public void handleMessage(Message message) {
        String consumerQueue = message.getMessageProperties().getConsumerQueue();
        LOGGER.info("Message has been received in {} queue: '{}'", consumerQueue, new String(message.getBody()));

        DataRetentionMessage dataRetentionMessage;
        try {
            dataRetentionMessage = objectMapper.readValue(message.getBody(), DataRetentionMessage.class);
        } catch (IOException e) {
            LOGGER.error("Can't deserialize received message: " + message.toString());
            return;
        }

        dataRetentionService.purgeData(dataRetentionMessage.getProperties().getTimestamp(), dataRetentionMessage.getProperties().getRetentionDurationDays());
    }
}
