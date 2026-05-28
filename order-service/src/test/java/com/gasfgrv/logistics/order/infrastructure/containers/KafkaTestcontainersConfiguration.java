package com.gasfgrv.logistics.order.infrastructure.containers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class KafkaTestcontainersConfiguration {

    private static final Network network = Network.newNetwork();

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    public ConfluentKafkaContainer kafkaContainer() {
        return new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
                .withNetwork(network)
                .withListener("kafka:19092");
    }

    @Bean
    @DependsOn("kafkaContainer")
    @SuppressWarnings("resource")
    public GenericContainer<?> schemaRegistryContainer(ConfluentKafkaContainer kafkaContainer) {
        return new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry:7.5.0"))
                .withExposedPorts(8081)
                .withNetwork(network)
                .withNetworkAliases("schema-registry")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_CUB_KAFKA_MIN_BROKERS", "1")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:19092")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .waitingFor(Wait.forHttp("/subjects").forStatusCode(200));
    }

    @Bean
    public DynamicPropertyRegistrar setKafkaProperties(ConfluentKafkaContainer kafkaContainer,
                                                       GenericContainer<?> schemaRegistryContainer) {
        return registry -> {
            String schemaRegistryUrl = "http://%s:%d"
                    .formatted(schemaRegistryContainer.getHost(), schemaRegistryContainer.getFirstMappedPort());
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
            registry.add("spring.kafka.producer.properties.schema.registry.url", () -> schemaRegistryUrl);
            registry.add("spring.kafka.properties.schema.registry.url", () -> schemaRegistryUrl);
        };
    }

}
