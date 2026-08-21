package com.gasfgrv.logistics.freight.infrastructure.messaging.listeners;

import com.gasfgrv.logistics.freight.domain.models.order.Order;
import com.gasfgrv.logistics.freight.domain.ports.in.CalculateOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.infrastructure.configurations.containers.KafkaTestcontainersConfiguration;
import com.gasfgrv.logistics.freight.infrastructure.mappers.OrderMapper;
import com.logistics.order.avro.v1.OrderCreatedEvent;
import org.apache.avro.specific.SpecificRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import java.util.UUID;

@Import(KafkaTestcontainersConfiguration.class)
@SpringBootTest(
        properties = {"spring.kafka.consumer.auto-offset-reset=earliest"},
        classes = {OrderCreatedListener.class}
)
class OrderCreatedListenerTest {

    @Autowired
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @Autowired
    private KafkaContainer kafkaContainer;

    @MockitoBean
    private CalculateOrderFreightUsecasePort usecase;

    @MockitoBean
    private OrderMapper mapper;

    @MockitoBean
    private org.springframework.boot.kafka.autoconfigure.KafkaProperties kafkaProperties;

    @Value("${kafka.topics.order-created}")
    private String topic;

    @Test
    void shouldConsumeOrderCreatedEvent() {
        OrderCreatedEvent payload = new OrderCreatedEvent();
        String eventId = payload.getOrderId().toString();

        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, topic);
        headers.put("eventId", eventId.getBytes(StandardCharsets.UTF_8));
        headers.put("eventType", "ORDER_CREATED".getBytes(StandardCharsets.UTF_8));
        headers.put("occurredAt", getCurrentTimeBytes());

        GenericMessage<OrderCreatedEvent> message = new GenericMessage<>(payload, headers);

        Order orderDomain = Order.builder().id(UUID.randomUUID()).build();
        when(mapper.toDomain(any(OrderCreatedEvent.class))).thenReturn(orderDomain);

        when(kafkaProperties.getBootstrapServers()).thenReturn(java.util.List.of(kafkaContainer.getBootstrapServers()));

        kafkaTemplate.send(message).join();

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(mapper).toDomain(payload);
                    verify(usecase).calculate(orderDomain);
                });
    }

    private byte[] getCurrentTimeBytes() {
        return Instant.now()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .getBytes(StandardCharsets.UTF_8);
    }

}