package com.gasfgrv.logistics.order.infrastructure.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.producer.properties.schema.registry")
public record SchemaRegistryProperties(
        String url
) {
}
