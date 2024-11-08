package com.example.integration.integration.jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;

import com.example.integration.integration.file.FileCleanup;
import com.example.integration.integration.file.FileInbound;

import lombok.RequiredArgsConstructor;

/**
 * Integration Flow that receives messages from {@link FileInbound#fileInboundFlow},
 * forwarding the messages to an ActiveMQ destination based on the jms_destination header.
 * Upon successful completion, it invokes {@link FileCleanup#fileCleanupFlow} to archive the processed files.
 */
@RequiredArgsConstructor
@Configuration
public class JmsOutbound {

    @Value("${xml.storage.path}")
    private String storagePath;

    private final ActiveMQConnectionFactory connectionFactory;
    private final DirectChannel fileSendToJmsChannel;
    private final DirectChannel fileCleanupChannel;

    @Bean
    public IntegrationFlow jmsOutboundFlow() {
        return IntegrationFlow
                .from(fileSendToJmsChannel)
                .log(LoggingHandler.Level.INFO, "jms.outbound.flow", m ->
                        "Forwarding to JMS queue: " + m.getHeaders().get("jms_destination"))
                .wireTap(p -> p.handle(Jms.outboundAdapter(connectionFactory)
                        .destinationExpression("headers.jms_destination")))
                .channel(fileCleanupChannel)
                .get();
    }
}
