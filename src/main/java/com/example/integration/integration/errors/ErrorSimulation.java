package com.example.integration.integration.errors;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import com.example.integration.controller.MessageController;

import lombok.RequiredArgsConstructor;

/**
 * A class designed to simulate error handling in Spring Integration,
 * utilizing ActiveMQ and JMS Inbound Gateway to return the appropriate message.
 * Triggered by {@link MessageController#simulateErrorFlow()}.
 */
@RequiredArgsConstructor
@Configuration
public class ErrorSimulation {

    private final ActiveMQConnectionFactory connectionFactory;

    private AtomicInteger counter = new AtomicInteger(0);

    @Bean
    public DirectChannel errorSimulationChannel() {
        return new DirectChannel();
    }
    @Bean
    public DirectChannel requestChannel() {
        return new DirectChannel();
    }
    @Bean
    public DirectChannel replyChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundGatewayFlow() {
        return IntegrationFlow
                .from(Jms.inboundGateway(connectionFactory)
                        .requestDestination("errorSimulationQueue")
                        .replyChannel(replyChannel())
                        .errorChannel(errorSimulationChannel())
                )
                .channel(requestChannel())
                .get();
    }

    @Bean
    public IntegrationFlow processFlow() {
        return IntegrationFlow
                .from(requestChannel())
                .transform(Message.class, this::handleMessage)
                .log(LoggingHandler.Level.INFO, "error.simulation.flow", m -> "No error thrown!")
                .channel(replyChannel())
                .get();
    }

    @Bean
    public IntegrationFlow errorSimulationFlow() {
        return IntegrationFlow
                .from(errorSimulationChannel())
                .transform(message -> ((MessagingException) message).getCause().getMessage())
                .log(LoggingHandler.Level.INFO, "error.simulation.flow", m -> "Error occurred: " + m.getPayload())
                .channel(replyChannel())
                .get();
    }

    private Message<String> handleMessage(Message<?> message) {
        int count = counter.incrementAndGet();
        if (count % 2 == 0) {
            throw new RuntimeException("Simulated error handled!");
        }
        return MessageBuilder
                .withPayload("No error thrown!")
                .copyHeaders(message.getHeaders())
                .build();
    }
}
