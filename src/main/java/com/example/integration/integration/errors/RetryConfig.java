package com.example.integration.integration.errors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Retry configuration for Integration flows.
 */
@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Retry policy: retry 3 times with exponential backoff
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000); // 1 second
        backOffPolicy.setMultiplier(2.0);       // double the interval each time
        backOffPolicy.setMaxInterval(10000);     // max 10 seconds

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));

        return retryTemplate;
    }

    @Bean
    public RequestHandlerRetryAdvice retryAdvice(RetryTemplate retryTemplate) {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        advice.setRetryTemplate(retryTemplate);
        return advice;
    }
}
