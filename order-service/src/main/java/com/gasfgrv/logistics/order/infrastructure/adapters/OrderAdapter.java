package com.gasfgrv.logistics.order.infrastructure.adapters;

import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.domain.ports.out.OrderPort;
import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.repositories.OrderRepository;
import com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers.OrderCancelledProducer;
import com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers.OrderCreatedProducer;
import com.gasfgrv.logistics.order.infrastructure.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAdapter implements OrderPort {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderCreatedProducer orderCreatedProducer;
    private final OrderCancelledProducer orderCancelledProducer;

    @Override
    public void save(Order order) {
        log.info("Saving order data to the table");
        repository.save(mapper.toEntity(order));
    }

    @Override
    public void sendOrder(Order order) {
        orderCreatedProducer.sendMessage(order);
    }

    @Override
    public Optional<Order> getOrderInfo(UUID orderId) {
        return repository.query(orderId).map(mapper::toDomain);
    }

    @Override
    public void notifyCancellation(Order order, String reason) {
        orderCancelledProducer.sendMessage(order, reason);
    }

}
