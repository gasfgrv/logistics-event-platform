package com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers;

import com.gasfgrv.logistics.order.infrastructure.exceptions.KafkaProduceMessageException;
import com.gasfgrv.logistics.order.infrastructure.providers.CurrentTimeProvider;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseKafkaProducerTest {

    @Captor
    ArgumentCaptor<ProducerRecord<String, SpecificRecord>> captor;

    @Mock
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @Mock
    private CurrentTimeProvider timeProvider;

    @InjectMocks
    private BaseKafkaProducer baseKafkaProducer;

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should send message and enrich headers")
    void shouldSendMessageAndEnrichHeaders() {
        // Arrange
        String topic = "test-topic";
        String key = "test-key";
        SpecificRecord payload = mock(SpecificRecord.class);
        String eventType = "TEST_EVENT";
        String eventId = "test-id";
        byte[] timeBytes = "2023-10-10T10:00:00Z".getBytes(StandardCharsets.UTF_8);

        when(timeProvider.getCurrentTimeBytes()).thenReturn(timeBytes);

        CompletableFuture<SendResult<String, SpecificRecord>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        // Act
        baseKafkaProducer.send(topic, key, payload, eventType, eventId);

        // Assert
        captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        ProducerRecord<String, SpecificRecord> record = captor.getValue();
        assertThat(record.topic()).isEqualTo(topic);
        assertThat(record.key()).isEqualTo(key);
        assertThat(record.value()).isEqualTo(payload);

        assertThat(getHeaderValue(record, "eventId")).isEqualTo(eventId);
        assertThat(getHeaderValue(record, "eventType")).isEqualTo(eventType);
        assertThat(getHeaderValue(record, "occurredAt")).isEqualTo("2023-10-10T10:00:00Z");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw KafkaProduceMessageException when send fails")
    void shouldThrowExceptionWhenSendFails() {
        // Arrange
        String topic = "test-topic";
        String key = "test-key";
        SpecificRecord payload = mock(SpecificRecord.class);

        CompletableFuture<SendResult<String, SpecificRecord>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        when(timeProvider.getCurrentTimeBytes()).thenReturn(new byte[0]);

        // Act
        CompletableFuture<SendResult<String, SpecificRecord>> result =
                baseKafkaProducer.send(topic, key, payload, "TYPE", "ID");

        // Assert
        assertThatExceptionOfType(java.util.concurrent.CompletionException.class)
                .isThrownBy(result::join)
                .satisfies(e -> {
                    boolean hasProducerException = Arrays.stream(e.getSuppressed())
                            .anyMatch(s -> s instanceof KafkaProduceMessageException)
                            || (e.getCause() != null && Arrays.stream(e.getCause().getSuppressed())
                            .anyMatch(s -> s instanceof KafkaProduceMessageException));
                    assertThat(hasProducerException).isTrue();
                });
    }

    private String getHeaderValue(ProducerRecord<?, ?> record, String key) {
        Header header = record.headers().lastHeader(key);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }

}
