package com.gasfgrv.logistics.freight.infrastructure.configurations;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.MessagingMessageConverter;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Bean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(KafkaProperties properties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(properties.buildProducerProperties()));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> kafkaListenerContainerFactory(KafkaProperties properties) {
        DefaultKafkaConsumerFactory<String, SpecificRecord> consumerFactory = new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties());
        ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordMessageConverter(new MessagingMessageConverter());
        return factory;
    }

}
