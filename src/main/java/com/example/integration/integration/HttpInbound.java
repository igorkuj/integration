package com.example.integration.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.dsl.Http;

import lombok.extern.slf4j.Slf4j;

/**
 * This class demonstrates the basic usage of Spring Integration's HTTP inbound adapters and gateways
 * to handle incoming HTTP requests. It includes two integration flows:
 * <p>
 * 1. **HttpInboundAdapter Flow**:
 *    - Listens for POST requests at the `/httpInboundAdapter` endpoint
 *    - Logs the incoming request(handling logic should begin here)
 * <p>
 * 2. **HttpInboundGateway Flow**:
 *    - Listens for POST requests at the `/httpInboundGateway` endpoint
 *    - Applies handling/transforming logic
 *    - Forwards the message to the replyChannel which the Gateway uses to send the response.
 */
@Slf4j
@Configuration
public class HttpInbound {

    @Bean
    public IntegrationFlow httpInboundAdapter() {
        return IntegrationFlow
                .from(Http.inboundChannelAdapter("/httpInboundAdapter")
                        .requestMapping(r -> r.methods(HttpMethod.POST))
                        .requestPayloadType(String.class))
                .handle(m -> log.info("HttpInboundAdapter received request: {}", m))
                .get();
    }

    @Bean
    public IntegrationFlow httpInboundGateway() {
        return IntegrationFlow
                .from(Http.inboundGateway("/httpInboundGateway")
                        .requestMapping(r -> r.methods(HttpMethod.POST))
                        .requestPayloadType(String.class)
                        .replyChannel("httpReplyChannel"))
                .log(LoggingHandler.Level.INFO, "http.inbound.gateway", m -> "Payload before uppercase: " + m.getPayload())
                .transform(m -> String.valueOf(m).toUpperCase())
                .log(LoggingHandler.Level.INFO, "http.inbound.gateway", m -> "Payload after uppercase: " + m.getPayload())
                .channel("httpReplyChannel")
                .get();
    }

    @Bean
    public DirectChannel httpReplyChannel() {
        return new DirectChannel();
    }
}
