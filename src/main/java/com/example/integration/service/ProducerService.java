package com.example.integration.service;

import com.example.integration.model.XmlMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProducerService {

    @Value("${jms.createXmlMessageQueue}")
    private String destination;

    private final JmsTemplate jmsTemplate;
    private final XmlMapper xmlMapper;

    public void sendXmlMessage(XmlMessage message) throws JsonProcessingException {
        String xml = xmlMapper.writeValueAsString(message);

        jmsTemplate.convertAndSend(destination, xml);
    }
}
