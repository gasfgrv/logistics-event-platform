package com.gasfgrv.logistics.freight.infrastructure.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.dynamo")
public record DynamoDbProperties(
        String serviceEndpoint,
        String region
) { }
