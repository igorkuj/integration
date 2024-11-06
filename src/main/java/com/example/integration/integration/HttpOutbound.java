package com.example.integration.integration;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.support.MessageBuilder;

import com.example.integration.model.XmlMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * This class demonstrates the use of Spring Integration's HTTP outbound adapters and gateways
 * to integrate with external HTTP services. It includes two integration flows:
 * <p>
 * 1. **HttpOutboundAdapter Flow**:
 *    - Periodically (every 10 minutes, initial delay 5s) polls a message source to retrieve an `XmlMessage` payload
 *    - Uses an `Http.outboundChannelAdapter` to send the message to the `/messages` endpoint
 * <p>
 * 2. **HttpOutboundGateway Flow**:
 *    - Periodically (every 1 minute, initial delay 10s) polls a message source to retrieve an `XmlMessage` payload
 *    - Uses an `Http.outboundGateway` to send a GET request to the `/messages/error` endpoint
 *    - Receives a response from the external service
 */
@Slf4j
@Configuration
public class HttpOutbound {

    @Bean
    public IntegrationFlow httpOutboundAdapter() {
        return IntegrationFlow
                .from(this.newMessageSource(),
                        p -> p.poller(Pollers.fixedDelay(Duration.ofMinutes(10), Duration.ofSeconds(5))))
                .log(LoggingHandler.Level.INFO, log.getName(), m -> "HttpOutboundAdapter sending request: " + m.getPayload())
                .handle(Http
                        .outboundChannelAdapter("http://localhost:8080/messages"))
                .get();
    }

    @Bean
    public IntegrationFlow httpOutboundGateway() {
        return IntegrationFlow
                .from(this.newMessageSource(),
                        p -> p.poller(Pollers.fixedDelay(Duration.ofMinutes(1), Duration.ofSeconds(10))))
                .log(LoggingHandler.Level.INFO, log.getName(), m -> "HttpOutboundGateway sending request at: /messages/error ")
                .handle(Http.outboundGateway("http://localhost:8080/messages/error")
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(String.class))
                .handle(m -> log.info("HttpOutboundGateway received response: {}", m.getPayload()))
                .get();
    }

    private MessageSource<XmlMessage> newMessageSource() {
        XmlMessage message = new XmlMessage();
        message.setMessage("httpOutbound");
        message.setDestination("httpOutbound");

        return () -> MessageBuilder.withPayload(message).build();
    }
}
