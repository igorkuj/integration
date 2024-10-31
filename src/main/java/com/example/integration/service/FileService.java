package com.example.integration.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.stereotype.Service;

import com.example.integration.model.XmlMessage;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * File Service responsible for managing XML file operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${xml.storage.path}")
    private String storagePath;

    @Value("${xml.archive.path}")
    private String archivePath;

    private final XmlMapper xmlMapper;

    public void saveXmlToFile(String xmlContent) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS"));

        String filename = String.format("xml_message_%s.xml", timestamp);

        try {
            Path filePath = Paths.get(storagePath, filename);
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, xmlContent);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save XML file: " + e.getMessage(), e);
        }
    }

    public Message<String> transformXmlMessage(Message<?> message) {
        String payload = String.valueOf(message.getPayload());
        try {
            XmlMessage xmlMessage = xmlMapper.readValue(payload, XmlMessage.class);
            return MessageBuilder
                    .withPayload(payload)
                    .copyHeaders(message.getHeaders())
                    .setHeader("jms_destination", xmlMessage.getDestination())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML: " + e.getMessage(), e);
        }
    }

    public void archiveFile(Message<?> message) {
        File originalFile = (File) message.getHeaders().get("file_originalFile");
        if (originalFile != null && originalFile.exists()) {
            try {
                Path archivedFilePath = Paths.get(archivePath, originalFile.getName());
                Files.createDirectories(archivedFilePath.getParent());
                Files.move(originalFile.toPath(), archivedFilePath,
                        StandardCopyOption.REPLACE_EXISTING);
                log.info("Archived file: {}", originalFile.getName());
            } catch (IOException e) {
                throw new MessageHandlingException(message, "Failed to archive file", e);
            }
        }
    }
}
