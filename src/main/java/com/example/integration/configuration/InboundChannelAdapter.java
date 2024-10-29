package com.example.integration.configuration;

import com.example.integration.service.FileService;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class InboundChannelAdapter {

    @Value("${jms.createXmlMessageQueue}")
    private String sendDestination;

    private final FileService fileService;

    @Bean
    public IntegrationFlow jmsInboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination(sendDestination))
                .log(LoggingHandler.Level.INFO, "inbound.channel.adapter", m -> "Message received: " + m)
                .handle(m -> fileService.saveXmlToFile(String.valueOf(m.getPayload())))
                .get();
    }
}
