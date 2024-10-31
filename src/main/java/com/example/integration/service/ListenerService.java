package com.example.integration.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Listener Service that listens on specific ActiveMQ queues for demonstration.
 */
@Slf4j
@Service
public class ListenerService {

    @JmsListener(destination = "aaa")
    public void onMessage(String message) {
        log.info("Queue: aaa | Received message: {}", message);
    }

    @JmsListener(destination = "bbb")
    public void onMessage2(String message) {
        log.info("Queue: bbb | Received message: {}", message);
    }
}
