package com.gasfgrv.logistics.freight.infrastructure.messaging.listeners;

import com.gasfgrv.logistics.freight.domain.ports.in.CalculateOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.infrastructure.mappers.OrderMapper;
import com.logistics.order.avro.v1.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final CalculateOrderFreightUsecasePort usecase;
    private final OrderMapper mapper;

    @KafkaListener(topics = "${kafka.topics.order-created}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(Message<OrderCreatedEvent> message) {
        log.info("Starting consuming from topic - {}", message.toString());
        usecase.calculate(mapper.toDomain(message.getPayload()));
    }

}
