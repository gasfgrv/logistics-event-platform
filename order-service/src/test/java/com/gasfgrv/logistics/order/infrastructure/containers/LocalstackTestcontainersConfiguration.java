package com.gasfgrv.logistics.order.infrastructure.containers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class LocalstackTestcontainersConfiguration {

    @Bean
    @SuppressWarnings("resource")
    public GenericContainer<?> localStackContainer() {
        return new GenericContainer<>(DockerImageName.parse("localstack/localstack:4.14.0"))
                .withExposedPorts(4566)
                .withEnv("SERVICES", "dynamodb");
    }

    @Bean
    public DynamicPropertyRegistrar dynamicPropertyRegistrar(GenericContainer<?> localStack) {
        return registry -> {
            String endpoint = "http://%s:%d".formatted(localStack.getHost(), localStack.getMappedPort(4566));
            registry.add("aws.dynamo.service-endpoint", () -> endpoint);
            registry.add("aws.dynamo.region", () -> "us-east-1");
            registry.add("aws.dynamo.access-key", () -> "test");
            registry.add("aws.dynamo.secret-key", () -> "test");
        };
    }

}
