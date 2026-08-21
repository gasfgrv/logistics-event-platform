package com.gasfgrv.logistics.freight.infrastructure.messaging.listeners;

import com.gasfgrv.logistics.freight.domain.models.order.Order;
import com.gasfgrv.logistics.freight.domain.ports.in.CancellOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.infrastructure.configurations.containers.KafkaTestcontainersConfiguration;
import com.gasfgrv.logistics.freight.infrastructure.configurations.kafka.KafkaTestConfiguration;
import com.gasfgrv.logistics.freight.infrastructure.mappers.OrderMapper;
import com.gasfgrv.logistics.freight.infrastructure.mappers.OrderMapperImpl;
import com.logistics.order.avro.v1.OrderCanceledEvent;
import org.apache.avro.specific.SpecificRecord;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@Import(value = {KafkaTestcontainersConfiguration.class, KafkaTestConfiguration.class})
@SpringBootTest(classes = {OrderCancelledListener.class, OrderMapperImpl.class})
class OrderCancelledListenerTest {

    @Autowired
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @MockitoBean
    private CancellOrderFreightUsecasePort usecase;

    @MockitoSpyBean
    private OrderMapperImpl mapper;

    @Value("${kafka.topics.order-cancelled}")
    private String topic;

    @Test
    void shouldConsumeOrderCreatedEvent() {
        OrderCanceledEvent payload = buildPayload();
        String eventId = payload.getOrderId().toString();
        Map<String, Object> headers = buildHeaders(eventId);

        GenericMessage<OrderCanceledEvent> message = new GenericMessage<>(payload, headers);
        sendEvent(message);

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

                    verify(mapper).toDomain(message.getPayload());
                    verify(usecase).cancelOrderFreight(captor.capture());

                    Order value = captor.getValue();
                    assertEquals(message.getPayload().getOrderId(), value.getId().toString());
                });
    }

    private OrderCanceledEvent buildPayload() {
        return Instancio.of(OrderCanceledEvent.class)
                .set(Select.field(OrderCanceledEvent::getOrderId), UUID.randomUUID().toString())
                .generate(Select.field(OrderCanceledEvent::getReason), gen -> gen.text().loremIpsum().words(5))
                .create();
    }

    private Map<String, Object> buildHeaders(String eventId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, topic.getBytes(StandardCharsets.UTF_8));
        headers.put("eventId", eventId.getBytes(StandardCharsets.UTF_8));
        headers.put("eventType", "ORDER_CANCELLED".getBytes(StandardCharsets.UTF_8));
        headers.put("occurredAt", timestampToBytes());
        return headers;
    }

    private byte[] timestampToBytes() {
        return Instant.now()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .getBytes(StandardCharsets.UTF_8);
    }

    private void sendEvent(Message<OrderCanceledEvent> message) {
        SendResult<String, SpecificRecord> sendResult = kafkaTemplate.send(message).join();
        assertNotNull(sendResult.getRecordMetadata());
        assertTrue(sendResult.getRecordMetadata().hasOffset());
    }

}