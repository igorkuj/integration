package com.example.integration.controller;

import com.example.integration.model.XmlMessage;
import com.example.integration.service.ProducerService;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.jms.JMSException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final ProducerService producerService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public XmlMessage sendXmlMessage(@RequestBody XmlMessage xmlMessage) throws JsonProcessingException {
        producerService.sendXmlMessage(xmlMessage);
        return xmlMessage;
    }

    @GetMapping("/error")
    public ResponseEntity<String> simulateErrorFlow() throws JMSException {
        String response = producerService.simulateError();
        return ResponseEntity.ok(response);
    }
}
