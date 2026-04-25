package com.gasfgrv.logistics.order.infrastructure.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.topics")
public record KafkaTopicsProperties(
        String orderCreated,
        String orderCancelled
) {
}
