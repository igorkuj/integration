package com.example.integration.model;

import lombok.Data;

/**
 * XML message format used to communicate over ActiveMQ.
 */
@Data
public class XmlMessage {

    private String message;
    private String destination;
}
