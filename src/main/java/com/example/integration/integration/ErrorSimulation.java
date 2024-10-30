package com.example.integration.integration;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ErrorSimulation {

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
    public IntegrationFlow inboundGatewayFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Jms.inboundGateway(connectionFactory)
                        .requestDestination("errorSimulationQueue")
                        .replyChannel("replyChannel")
                        .errorChannel("errorSimulationChannel")
                )
                .channel("requestChannel")
                .get();
    }

    @Bean
    public IntegrationFlow processFlow() {
        return IntegrationFlow
                .from("requestChannel")
                .transform(Message.class, this::handleMessage)
                .channel("replyChannel")
                .get();
    }

    @Bean
    public IntegrationFlow errorSimulationFlow() {
        return IntegrationFlow
                .from("errorSimulationChannel")
                .transform(message -> {
                    String error = ((MessagingException) message).getCause().getMessage();
                    log.error("Error occurred: {}", error);
                    return error;
                })
                .channel("replyChannel")
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
