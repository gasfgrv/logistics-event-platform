package com.gasfgrv.logistics.freight.infrastructure.configurations.kafka;


import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.util.List;

@EnableKafka
@TestConfiguration(proxyBeanMethods = false)
public class KafkaTestConfiguration {

    @Bean
    public KafkaProperties kafkaProperties(ConfluentKafkaContainer kafkaContainer, GenericContainer<?> schemaRegistryContainer) {
        KafkaProperties properties = new KafkaProperties();
        properties.setBootstrapServers(List.of(kafkaContainer.getBootstrapServers()));

        String schemaRegistryUrl = "http://%s:%d"
                .formatted(schemaRegistryContainer.getHost(), schemaRegistryContainer.getMappedPort(8081));
        properties.getProperties().put("schema.registry.url", schemaRegistryUrl);

        properties.getProducer().getProperties().put("schema.registry.url", schemaRegistryUrl);
        properties.getProducer().getProperties().put("specific.avro.reader", "true");
        properties.getProducer().getProperties().put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.getProducer().getProperties().put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

        properties.getConsumer().getProperties().put("schema.registry.url", schemaRegistryUrl);
        properties.getConsumer().getProperties().put("specific.avro.reader", "true");
        properties.getConsumer().getProperties().put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.getConsumer().getProperties().put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.getConsumer().getProperties().put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.getConsumer().getProperties().put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return properties;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> kafkaListenerContainerFactory(KafkaProperties properties) {
        DefaultKafkaConsumerFactory<String, SpecificRecord> consumerFactory = new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties());
        ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setRecordMessageConverter(new MessagingMessageConverter());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(KafkaProperties properties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(properties.buildProducerProperties()));
    }

}
