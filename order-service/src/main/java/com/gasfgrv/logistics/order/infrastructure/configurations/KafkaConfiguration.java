package com.gasfgrv.logistics.order.infrastructure.configurations;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Bean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(KafkaProperties properties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(properties.buildProducerProperties()));
    }

}
