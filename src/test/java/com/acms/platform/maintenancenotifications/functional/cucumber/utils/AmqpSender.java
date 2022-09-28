package com.acms.platform.maintenancenotifications.functional.cucumber.utils;

import com.alliance.platform.amqptracing.sender.TracedRabbitSender;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static com.acms.platform.maintenancenotifications.functional.cucumber.utils.MessageCreator.createMessage;


@Component
public class AmqpSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpSender.class);

    private final TracedRabbitSender rabbitSender;

    public AmqpSender(TracedRabbitSender rabbitSender) {
        this.rabbitSender = rabbitSender;
    }

    public void send(String msg, String exchange, String routingKey, Map<String, String> headers, Variable... variables) {

        Message message = createMessage(msg, variables);
        Map<String, Object> msgHeaders = message.getMessageProperties().getHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            msgHeaders.put(entry.getKey(), entry.getValue());
        }
        LOGGER.info("##################### Sending message {} to: {} - {}", message, exchange, routingKey);
        rabbitSender.send(exchange, routingKey, message);
    }

    public void send(Resource input, String routingKey, String exchange) {
        String byteMessage;
        try {
            byteMessage = IOUtils.toString(input.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Unable to read resource");
            return;
        }

        send(byteMessage, exchange, routingKey, Collections.emptyMap());
    }
}
