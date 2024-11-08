package com.example.integration.integration.file;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.LoggingHandler;

import com.example.integration.integration.jms.JmsInbound;

/**
 * Integration Flow that receives messages from {@link JmsInbound#jmsInboundFlow} and writes the files locally.
 */
@Configuration
public class FileOutbound {

    @Value("${xml.storage.path}")
    private String storagePath;

    @Bean
    public DirectChannel writeFileChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow fileOutboundFlow() {
        return IntegrationFlow
                .from(writeFileChannel())
                .log(LoggingHandler.Level.INFO, "file.outbound.flow", m -> "Writing file to: " + storagePath.replace("//", "\\"))
                .handle(Files.outboundAdapter("'" + storagePath + "'")
                        .fileNameGenerator(this::generateFileName)
                        .autoCreateDirectory(true)
                        .fileExistsMode(FileExistsMode.IGNORE))
                .get();
    }

    private String generateFileName(Object payload) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS"));
        return String.format("xml_message_%s.xml", timestamp);
    }
}
