package com.example.integration.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;

import com.example.integration.service.FileService;

import lombok.RequiredArgsConstructor;

/**
 * Integration Flow that archives files after successful {@link JmsOutbound#jmsOutboundFlow}.
 */
@RequiredArgsConstructor
@Configuration
public class FileCleanup {

    private final FileService fileService;

    @Bean
    public DirectChannel fileCleanupChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow fileCleanupFlow() {
        return IntegrationFlow
                .from(fileCleanupChannel())
                .handle(fileService::archiveFile)
                .get();
    }
}
