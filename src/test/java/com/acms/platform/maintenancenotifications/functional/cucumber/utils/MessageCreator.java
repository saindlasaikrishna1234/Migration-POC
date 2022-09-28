package com.acms.platform.maintenancenotifications.functional.cucumber.utils;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePropertiesBuilder;

import java.util.Collections;
import java.util.Map;

public class MessageCreator {

    public static Message createMessage(String msg) {
        return createMessage(msg, Collections.emptyMap());
    }

    public static Message createMessage(String msg, Variable... variables) {
        return createMessage(msg, Collections.emptyMap(), variables);
    }

    public static Message createMessage(String msg, Map<String, String> headers, Variable... variables) {

        for (Variable variable : variables) {
            msg = msg.replace("${" + variable.getKey() + "}", variable.getValue());
        }

        MessagePropertiesBuilder messagePropertiesBuilder = MessagePropertiesBuilder.newInstance();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            messagePropertiesBuilder.setHeader(entry.getKey(), entry.getValue());
        }
        messagePropertiesBuilder.setContentType("application/json");

        return MessageBuilder
                .withBody(msg.getBytes())
                .andProperties(messagePropertiesBuilder.build())
                .build();
    }

}
