package com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers;

import com.gasfgrv.logistics.order.infrastructure.exceptions.KafkaProduceMessageException;
import com.gasfgrv.logistics.order.infrastructure.providers.CurrentTimeProvider;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
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
import java.util.concurrent.CompletionException;

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
        var topic = "test-topic";
        var key = "test-key";
        var payload = mock(SpecificRecord.class);
        var eventType = "TEST_EVENT";
        var eventId = "test-id";
        var timeBytes = "2023-10-10T10:00:00Z".getBytes(StandardCharsets.UTF_8);

        when(timeProvider.getCurrentTimeBytes()).thenReturn(timeBytes);

        var future = new CompletableFuture<SendResult<String, SpecificRecord>>();
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        // Act
        baseKafkaProducer.send(topic, key, payload, eventType, eventId);

        // Assert
        captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        var record = captor.getValue();
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
        var topic = "test-topic";
        var key = "test-key";
        var payload = mock(SpecificRecord.class);

        var future = new CompletableFuture<SendResult<String, SpecificRecord>>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        when(timeProvider.getCurrentTimeBytes()).thenReturn(new byte[0]);

        // Act
        var result = baseKafkaProducer.send(topic, key, payload, "TYPE", "ID");

        // Assert
        assertThatExceptionOfType(java.util.concurrent.CompletionException.class)
                .isThrownBy(result::join)
                .satisfies(e -> {
                    boolean hasProducerException = hasSomeKafkaProducerMessageException(e.getSuppressed())
                            || causeHasSomeKafkaProducerMessageException(e);
                    assertThat(hasProducerException).isTrue();
                });
    }

    private boolean causeHasSomeKafkaProducerMessageException(CompletionException e) {
        return e.getCause() != null && hasSomeKafkaProducerMessageException(e.getCause().getSuppressed());
    }

    private boolean hasSomeKafkaProducerMessageException(Throwable[] e) {
        return Arrays.stream(e).anyMatch(throwable -> throwable instanceof KafkaProduceMessageException);
    }

    private String getHeaderValue(ProducerRecord<?, ?> record, String key) {
        var header = record.headers().lastHeader(key);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }

}
