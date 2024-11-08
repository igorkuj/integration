package com.example.integration.integration.jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;

import com.example.integration.integration.file.FileOutbound;

import lombok.RequiredArgsConstructor;

/**
 * Integration Flow that receives messages from ActiveMQ destination and forwards them to {@link FileOutbound#fileOutboundFlow}.
 */
@RequiredArgsConstructor
@Configuration
public class JmsInbound {

    @Value("${jms.createXmlMessageQueue}")
    private String sendDestination;

    private final ActiveMQConnectionFactory connectionFactory;
    private final DirectChannel writeFileChannel;

    @Bean
    public IntegrationFlow jmsInboundFlow() {
        return IntegrationFlow
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination(sendDestination)
                        .errorChannel("errorChannel")) // if not set, "errorChannel" is default
                .log(LoggingHandler.Level.INFO, "jms.inbound.flow", m -> "JMS Inbound -> File Outbound: " + m)
                .channel(writeFileChannel )
                .get();
    }
}
