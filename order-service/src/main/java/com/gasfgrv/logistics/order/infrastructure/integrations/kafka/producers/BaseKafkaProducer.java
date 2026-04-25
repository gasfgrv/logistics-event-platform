package com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers;

import com.gasfgrv.logistics.order.infrastructure.exceptions.KafkaProduceMessageException;
import com.gasfgrv.logistics.order.infrastructure.providers.CurrentTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseKafkaProducer {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    private final CurrentTimeProvider timeProvider;

    public void send(String topic, String key, SpecificRecord payload, String eventType, String eventId) {
        log.info("Sending message to topic: {}", topic);
        var record = new ProducerRecord<>(topic, key, payload);
        enrichHeaders(eventType, eventId, record);
        kafkaTemplate.send(record)
                .whenComplete(BaseKafkaProducer::printSendStatus);
    }

    private void enrichHeaders(String eventType, String eventId, ProducerRecord<String, SpecificRecord> record) {
        record.headers()
                .add("eventId", eventId.getBytes(StandardCharsets.UTF_8))
                .add("eventType", eventType.getBytes(StandardCharsets.UTF_8))
                .add("occurredAt", timeProvider.getCurrentTimeBytes());
    }

    private static void printSendStatus(SendResult<String, SpecificRecord> result, Throwable ex) {
        if (ex != null) {
            log.info("Error sending message: {}", ex.getMessage());
            throw new KafkaProduceMessageException(ex.getMessage());
        }

        log.info("Message sent successfully to topic: {}", result.getRecordMetadata().topic());
    }

}
