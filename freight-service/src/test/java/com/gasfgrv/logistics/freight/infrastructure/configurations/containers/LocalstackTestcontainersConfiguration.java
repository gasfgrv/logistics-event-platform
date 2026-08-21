package com.gasfgrv.logistics.freight.infrastructure.configurations.containers;

import com.gasfgrv.logistics.freight.infrastructure.persistence.entities.FreightEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@TestConfiguration(proxyBeanMethods = false)
public class LocalstackTestcontainersConfiguration {

    @Bean
    @SuppressWarnings("resource")
    public LocalStackContainer localstackContainer() {
        return new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.14.0"))
                .withExposedPorts(4566)
                .withServices("dynamodb");
    }

    @Bean
    public DynamicPropertyRegistrar dynamicPropertyRegistrar(LocalStackContainer localStackContainer) {
        var endpoint = localStackContainer.getEndpoint().toString();
        return registry -> {
            registry.add("aws.dynamo.service-endpoint", () -> endpoint);
            registry.add("aws.dynamo.region", localStackContainer::getRegion);
            registry.add("aws.dynamo.access-key", localStackContainer::getAccessKey);
            registry.add("aws.dynamo.secret-key", localStackContainer::getSecretKey);
        };
    }

    @Bean
    public CommandLineRunner createDynamoTables(DynamoDbEnhancedClient enhancedClient) {
        return args -> {
            DynamoDbTable<FreightEntity> freights = enhancedClient.table("freights",
                    TableSchema.fromBean(FreightEntity.class));
            freights.createTable();
        };
    }

}
