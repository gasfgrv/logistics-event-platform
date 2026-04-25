package com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers;

import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.infrastructure.configurations.properties.KafkaTopicsProperties;
import com.gasfgrv.logistics.order.infrastructure.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledProducer {

    private final BaseKafkaProducer producer;
    private final OrderMapper mapper;
    private final KafkaTopicsProperties topics;

    public void sendMessage(Order order, String reason) {
        var message = mapper.toCanceledEvent(order, reason);
        producer.send(topics.orderCancelled(), order.getIdValue(), message, "ORDER_CANCELLED", order.getIdValue());
    }

}
