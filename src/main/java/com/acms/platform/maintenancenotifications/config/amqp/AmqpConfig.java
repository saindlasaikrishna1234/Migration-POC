package com.acms.platform.maintenancenotifications.config.amqp;

import com.alliance.platform.amqptracing.postprocessor.RabbitReceiveTracingPostProcess;
import com.alliance.platform.amqptracing.sender.TracedRabbitSender;
import com.alliance.platform.amqptracing.sender.TracedRabbitSenderImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

@Configuration
@EnableRabbit
public class AmqpConfig {

    private final String instanceIndex;
    private final String appName;
    private final TelemetryClient telemetryClient;

    public AmqpConfig(@Value("${instance.index:0}") String instanceIndex,
                      @Value("${spring.application.name}") String appName,
                      TelemetryClient telemetryClient) {
        this.instanceIndex = instanceIndex;
        this.appName = appName;
        this.telemetryClient = telemetryClient;
    }

    @Bean("rabbitListenerContainerFactory")
    @Primary
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory,
                                                                               SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        return getSimpleRabbitListenerContainerFactory(rabbitConnectionFactory, configurer);
    }

    @Bean("amqpObjectMapper")
    public ObjectMapper amqpObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    @Qualifier("jsonRabbitTemplate")
    public RabbitTemplate jsonRabbitTemplate(ConnectionFactory rabbitConnectionFactory) {
        return new RabbitTemplate(rabbitConnectionFactory);
    }

    @Bean
    public TracedRabbitSender tracedRabbitSender(RabbitTemplate jsonRabbitTemplate) {
        return new TracedRabbitSenderImpl(jsonRabbitTemplate, telemetryClient, appName);
    }

    @Bean
    public ConnectionNameStrategy connectionNameStrategy() {
        return connectionFactory -> getName(appName, instanceIndex);
    }

    private SimpleRabbitListenerContainerFactory getSimpleRabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory,
                                                                                         SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, rabbitConnectionFactory);
        factory.setErrorHandler(new ConditionalRejectingErrorHandler(t -> true));
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setConsumerTagStrategy(s -> getName(appName, instanceIndex));
        factory.setAfterReceivePostProcessors(new RabbitReceiveTracingPostProcess(telemetryClient, appName));
        return factory;
    }

    private String getName(String appName, String cfInstanceIndex) {
        final String uuid = UUID.randomUUID().toString();
        return String.format("%s.%s.%s", appName, cfInstanceIndex, uuid);
    }
}
