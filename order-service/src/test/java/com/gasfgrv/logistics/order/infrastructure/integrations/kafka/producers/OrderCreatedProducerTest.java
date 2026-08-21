package com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers;

import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.infrastructure.configurations.properties.KafkaTopicsProperties;
import com.gasfgrv.logistics.order.infrastructure.containers.KafkaTestcontainersConfiguration;
import com.gasfgrv.logistics.order.infrastructure.mappers.OrderMapper;
import com.logistics.order.avro.v1.OrderCreatedEvent;
import org.instancio.Instancio;
import org.instancio.Select;
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
class OrderCreatedProducerTest {

    @Autowired
    private OrderCreatedProducer orderCreatedProducer;

    @MockitoSpyBean
    private BaseKafkaProducer baseKafkaProducer;

    @MockitoBean
    private OrderMapper mapper;

    @Autowired
    private KafkaTopicsProperties topics;

    @Test
    @DisplayName("Should send message through Kafka infrastructure")
    void shouldSendMessageThroughKafkaInfra() {
        // Arrange
        var order = Instancio.of(Order.class)
                .set(Select.field(Order::getId), UUID.randomUUID())
                .create();

        var pattern = "#d#d#d#d#d-#d#d#d";
        var event = Instancio.of(OrderCreatedEvent.class)
                .set(Select.field(OrderCreatedEvent::getOrderId), order.getId().toString())
                .set(Select.field(OrderCreatedEvent::getCustomerId), UUID.randomUUID().toString())
                .generate(Select.field(OrderCreatedEvent::getOrigin), gen -> gen.text().pattern(pattern))
                .generate(Select.field(OrderCreatedEvent::getDestination), gen -> gen.text().pattern(pattern))
                .generate(Select.field(OrderCreatedEvent::getWeight), gen -> gen.doubles().range(1.0, 100.0))
                .create();

        when(mapper.toCreatedEvent(order)).thenReturn(event);

        // Act
        orderCreatedProducer.sendMessage(order);

        // Assert
        verify(baseKafkaProducer).send(
                eq(topics.orderCreated()),
                eq(order.getIdValue()),
                eq(event),
                eq("ORDER_CREATED"),
                eq(order.getIdValue())
        );
    }

}
