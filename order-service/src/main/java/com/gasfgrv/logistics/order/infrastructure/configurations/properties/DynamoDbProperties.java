package com.gasfgrv.logistics.order.infrastructure.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.dynamo")
public record DynamoDbProperties(
        String serviceEndpoint,
        String region
) {
}
