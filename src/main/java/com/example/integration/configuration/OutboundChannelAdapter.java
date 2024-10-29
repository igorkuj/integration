package com.example.integration.configuration;

import java.io.File;

import com.example.integration.service.FileService;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.Message;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class OutboundChannelAdapter {

    @Value("${xml.storage.path}")
    private String storagePath;

    private final FileService fileService;

    @Bean
    public IntegrationFlow jmsOutboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Files.inboundAdapter(new File(storagePath))
                                .patternFilter("*.xml"),
                        e -> e.poller(Pollers.fixedDelay(1000)))
                .log(LoggingHandler.Level.INFO, "outbound.channel.adapter", m -> "Reading XML file: " + m)
                .transform(Files.toStringTransformer())
                .log(LoggingHandler.Level.INFO, "outbound.channel.adapter", m -> "XML payload: " + m)
                .transform(Message.class, fileService::transformXmlMessage)
                .log(LoggingHandler.Level.INFO, "outbound.channel.adapter",
                        m -> "Sending to queue: " + m.getHeaders().get("jms_destination"))
                .wireTap(p -> p.handle(Jms.outboundAdapter(connectionFactory)
                        .destinationExpression("headers.jms_destination")))
                .channel("fileCleanupChannel")
                .get();
    }

    @Bean
    public DirectChannel fileCleanupChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow fileCleanupFlow() {
        return IntegrationFlow
                .from("fileCleanupChannel")
                .handle(fileService::archiveFile)
                .get();
    }
}
