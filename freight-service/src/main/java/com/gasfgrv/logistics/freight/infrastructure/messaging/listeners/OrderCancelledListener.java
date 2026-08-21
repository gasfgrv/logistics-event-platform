package com.gasfgrv.logistics.freight.infrastructure.messaging.listeners;

import com.gasfgrv.logistics.freight.domain.ports.in.CancellOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.infrastructure.mappers.OrderMapper;
import com.logistics.order.avro.v1.OrderCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledListener {

    private final CancellOrderFreightUsecasePort usecase;
    private final OrderMapper mapper;

    @KafkaListener(topics = "${kafka.topics.order-cancelled}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(Message<OrderCanceledEvent> message, Acknowledgment acknowledgment) {
        try {
            log.info("Starting consuming from topic - {}", message.toString());
            usecase.cancelOrderFreight(mapper.toDomain(message.getPayload()));
        } catch (Exception e) {
            log.error("Error occurred while consuming message from topic - {}", message.toString(), e);
            throw new RuntimeException(e);
        } finally {
            acknowledgment.acknowledge();
        }
    }

}
