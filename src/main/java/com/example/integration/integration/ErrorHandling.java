package com.example.integration.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import lombok.extern.slf4j.Slf4j;

/**
 * Error handling configuration for Integration flows.
 */
@Slf4j
@Configuration
public class ErrorHandling {

    @Bean
    public DirectChannel errorChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlow
                .from("errorChannel")
                .handle(this::logError)
                .get();
    }

    private void logError(Message<?> message) {
        log.error("Error message:");
        log.error("Payload: {}", ((MessagingException) message.getPayload()).getFailedMessage());
        log.error("Cause: {}", String.valueOf(((MessagingException) message.getPayload()).getCause()));
    }
}
