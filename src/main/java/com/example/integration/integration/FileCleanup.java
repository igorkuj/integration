package com.example.integration.integration;

import com.example.integration.service.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;

import lombok.RequiredArgsConstructor;

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
                .from("fileCleanupChannel")
                .handle(fileService::archiveFile)
                .get();
    }
}
