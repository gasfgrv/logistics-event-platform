package com.gasfgrv.logistics.order.infrastructure.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viacep.api")
public record ViacepApiProperties(
        String baseUrl
) {
}
