package com.example.integration.integration.file;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;

import com.example.integration.service.FileService;
import com.example.integration.integration.errors.RetryConfig;

import lombok.RequiredArgsConstructor;

/**
 * Integration Flow that polls a file directory for new files created by {@link FileOutbound#fileOutboundFlow}.
 * It reads and converts the files, forwarding the messages to a JMS Flow adding the jms_destination header based on <destination/>.
 * Uses the default errorChannel for error handling and {@link RetryConfig#retryAdvice} for retry configuration.
 */
@RequiredArgsConstructor
@Configuration
public class FileInbound {

    @Value("${xml.storage.path}")
    private String storagePath;

    private final FileService fileService;

    @Bean
    public IntegrationFlow fileInboundFlow() {
        return IntegrationFlow
                .from(Files.inboundAdapter(new File(storagePath)).patternFilter("*.xml"),
                        e -> e.poller(Pollers.fixedDelay(1000)
                                .errorChannel("errorChannel"))) // if not set, "errorChannel" is default
                .log(LoggingHandler.Level.INFO, "file.inbound.flow", m -> "Reading XML file: " + m.getPayload())
                .transform(Files.toStringTransformer())
                .log(LoggingHandler.Level.INFO, "file.inbound.flow", m -> "XML payload: " + m.getPayload())
                .transform(Message.class, fileService::transformXmlMessage)
                .log(LoggingHandler.Level.INFO, "file.inbound.flow", m -> "Sending to JMS Outbound Flow, added jms_destination header: "
                        + m.getHeaders().get("jms_destination"))
                .channel(fileSendToJmsChannel())
                .get();
    }

    @Bean
    public DirectChannel fileSendToJmsChannel() {
        return new DirectChannel();
    }
}
