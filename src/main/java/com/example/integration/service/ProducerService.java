package com.example.integration.service;

import com.example.integration.model.XmlMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProducerService {

    @Value("${jms.createXmlMessageQueue}")
    private String destination;

    @Value("${jms.errorSimulationQueue}")
    private String errorDestination;

    private final JmsTemplate jmsTemplate;
    private final XmlMapper xmlMapper;

    public void sendXmlMessage(XmlMessage message) throws JsonProcessingException {
        String xml = xmlMapper.writeValueAsString(message);

        jmsTemplate.convertAndSend(destination, xml);
    }

    public String simulateError() throws JMSException {
        TextMessage response = (TextMessage) jmsTemplate.sendAndReceive(errorDestination, session -> session.createTextMessage("Error handling test"));
        if (response == null) {
            return null;
        }
        return response.getText();
    }
}
