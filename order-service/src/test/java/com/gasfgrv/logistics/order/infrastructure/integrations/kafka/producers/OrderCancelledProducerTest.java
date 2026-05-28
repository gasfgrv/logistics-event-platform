package com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers;

import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.infrastructure.configurations.properties.KafkaTopicsProperties;
import com.gasfgrv.logistics.order.infrastructure.containers.KafkaTestcontainersConfiguration;
import com.gasfgrv.logistics.order.infrastructure.mappers.OrderMapper;
import com.logistics.order.avro.v1.OrderCanceledEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(KafkaTestcontainersConfiguration.class)
class OrderCancelledProducerTest {

    @Autowired
    private OrderCancelledProducer orderCancelledProducer;

    @MockitoSpyBean
    private BaseKafkaProducer baseKafkaProducer;

    @MockitoBean
    private OrderMapper mapper;

    @Autowired
    private KafkaTopicsProperties topics;

    @Test
    @DisplayName("Should send cancelled message through Kafka infrastructure")
    void shouldSendMessageThroughKafkaInfra() {
        // Arrange
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .build();
        String reason = "Cancelled by user";
        OrderCanceledEvent event = OrderCanceledEvent.newBuilder()
                .setOrderId(order.getIdValue())
                .setReason(reason)
                .build();

        when(mapper.toCanceledEvent(order, reason)).thenReturn(event);

        // Act
        orderCancelledProducer.sendMessage(order, reason);

        // Assert
        verify(baseKafkaProducer).send(
                eq(topics.orderCancelled()),
                eq(order.getIdValue()),
                eq(event),
                eq("ORDER_CANCELLED"),
                eq(order.getIdValue())
        );
    }

}
