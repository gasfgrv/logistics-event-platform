package com.gasfgrv.logistics.freight.infrastructure.messaging.listeners;

import com.gasfgrv.logistics.freight.domain.models.order.Order;
import com.gasfgrv.logistics.freight.domain.ports.in.CalculateOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.infrastructure.configurations.containers.KafkaTestcontainersConfiguration;
import com.gasfgrv.logistics.freight.infrastructure.configurations.kafka.KafkaTestConfiguration;
import com.gasfgrv.logistics.freight.infrastructure.mappers.OrderMapperImpl;
import com.logistics.order.avro.v1.OrderCreatedEvent;
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
@SpringBootTest(classes = {OrderCreatedListener.class, OrderMapperImpl.class})
class OrderCreatedListenerTest {

    @Autowired
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @MockitoBean
    private CalculateOrderFreightUsecasePort usecase;

    @MockitoSpyBean
    private OrderMapperImpl mapper;

    @Value("${kafka.topics.order-created}")
    private String topic;

    @Test
    void shouldConsumeOrderCreatedEvent() {
        OrderCreatedEvent payload = buildPayload();
        String eventId = payload.getOrderId().toString();
        Map<String, Object> headers = buildHeaders(eventId);

        GenericMessage<OrderCreatedEvent> message = new GenericMessage<>(payload, headers);
        sendEvent(message);

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

                    verify(mapper).toDomain(message.getPayload());
                    verify(usecase).calculate(captor.capture());

                    Order value = captor.getValue();
                    assertEquals(message.getPayload().getOrderId(), value.getId().toString());
                });
    }


    private OrderCreatedEvent buildPayload() {
        return Instancio.of(OrderCreatedEvent.class)
                .set(Select.field(OrderCreatedEvent::getOrderId), UUID.randomUUID().toString())
                .set(Select.field(OrderCreatedEvent::getCustomerId), UUID.randomUUID().toString())
                .generate(Select.field(OrderCreatedEvent::getOrigin), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(OrderCreatedEvent::getDestination), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(OrderCreatedEvent::getWeight), gen -> gen.doubles().range(0.1, 100.0))
                .create();
    }

    private Map<String, Object> buildHeaders(String eventId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, topic);
        headers.put("eventId", eventId.getBytes(StandardCharsets.UTF_8));
        headers.put("eventType", "ORDER_CREATED".getBytes(StandardCharsets.UTF_8));
        headers.put("occurredAt", timestampToBytes());
        return headers;
    }

    private byte[] timestampToBytes() {
        return Instant.now()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .getBytes(StandardCharsets.UTF_8);
    }

    private void sendEvent(GenericMessage<OrderCreatedEvent> message) {
        SendResult<String, SpecificRecord> sendResult = kafkaTemplate.send(message).join();
        assertNotNull(sendResult.getRecordMetadata());
        assertTrue(sendResult.getRecordMetadata().hasOffset());
    }

}
