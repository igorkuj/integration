package com.example.integration.integration;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.jms.dsl.Jms;

import com.example.integration.service.FileService;

import lombok.RequiredArgsConstructor;

/**
 * Integration Flow that receives messages through ActiveMQ and calls {@link FileService#saveXmlToFile} to write XML files locally.
 * Uses {@link ErrorHandling#errorChannel} for error handling and {@link RetryConfig#retryAdvice} for retry configuration.
 */
@RequiredArgsConstructor
@Configuration
public class JmsInbound {

    @Value("${jms.createXmlMessageQueue}")
    private String sendDestination;

    private final FileService fileService;
    private final DirectChannel errorChannel;
    private final RequestHandlerRetryAdvice retryAdvice;

    @Bean
    public IntegrationFlow jmsInboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination(sendDestination)
                        .errorChannel(errorChannel))
                .log(LoggingHandler.Level.INFO, "jms.inbound.flow", m -> "Message received: " + m)
                .handle(m -> fileService.saveXmlToFile(String.valueOf(m.getPayload())), e -> e.advice(retryAdvice))
                .get();
    }
}
