package com.example.integration.configuration;

import jakarta.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * JMS Configuration class.
 */
@Configuration
@EnableJms
public class JmsConfig {

    @Value("${jms.url}")
    private String jmsUrl;

    @Value("${jms.username}")
    private String jmsUsername;

    @Value("${jms.password}")
    private String jmsPassword;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(jmsUrl);
        connectionFactory.setUser(jmsUsername);
        connectionFactory.setPassword(jmsPassword);
        return connectionFactory;
    }

    @Bean
    public JmsTransactionManager transactionManager() throws JMSException {
        return new JmsTransactionManager(connectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() throws JMSException {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() throws JMSException {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setTransactionManager(transactionManager());
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(true);
//        factory.setMessageConverter(customMessageConverter());
        return factory;
    }

    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }
}
