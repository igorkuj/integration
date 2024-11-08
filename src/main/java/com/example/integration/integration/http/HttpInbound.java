package com.example.integration.integration.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.dsl.Http;

import com.example.integration.configuration.XmlHttpMessageConverter;
import com.example.integration.model.XmlMessage;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

/**
 * This class demonstrates the basic usage of Spring Integration's HTTP inbound adapters and gateways
 * to handle incoming HTTP requests. It includes two integration flows:
 * <p>
 * 1. **HttpInboundAdapter Flow**:
 *    - Listens for POST requests at the `/httpInboundAdapter` endpoint
 *    - Routes message based on destination property
 * <p>
 * 2. **HttpInboundGateway Flow**:
 *    - Listens for POST requests at the `/httpInboundGateway` endpoint
 *    - Applies handling/transforming logic
 *    - Forwards the message to the replyChannel which the Gateway uses to send the response.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class HttpInbound {

    private final JsonbHttpMessageConverter jsonbHttpMessageConverter = new JsonbHttpMessageConverter();
    private final XmlHttpMessageConverter xmlHttpMessageConverter;

    @Bean
    public IntegrationFlow httpInboundAdapter() {
        return IntegrationFlow
                .from(Http.inboundChannelAdapter("/httpInboundAdapter")
                        .requestMapping(r -> r.methods(HttpMethod.POST))
                        .requestPayloadType(XmlMessage.class)
                        .messageConverters(jsonbHttpMessageConverter, xmlHttpMessageConverter))
                .log(LoggingHandler.Level.INFO, log.getName(), m -> "HttpInboundAdapter received request: " + m)
                .log(LoggingHandler.Level.INFO, log.getName(), m -> "Routing to: " + ((XmlMessage) m.getPayload()).getDestination())
                .route("payload['destination']",
                    r -> r.suffix("Channel")
                            .channelMapping("destinationA", "destinationA")
                            .channelMapping("destinationB", "destinationB")
                            .defaultOutputChannel("defaultChannel"))
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
    public IntegrationFlow routedToDestinationAChannel() {
        return IntegrationFlow
                .from("destinationAChannel")
                .handle(p -> log.info("Message successfully routed to destinationAChannel!"))
                .get();
    }

    @Bean
    public IntegrationFlow routedToDestinationBChannel() {
        return IntegrationFlow
                .from("destinationBChannel")
                .handle(p -> log.info("Message successfully routed to destinationBChannel!"))
                .get();
    }

    @Bean
    public IntegrationFlow routedToDefaultChannel() {
        return IntegrationFlow
                .from("defaultChannel")
                .handle(p -> log.info("Channel: {} not found, routed to default channel!", ((XmlMessage) p.getPayload()).getDestination()))
                .get();
    }
}
