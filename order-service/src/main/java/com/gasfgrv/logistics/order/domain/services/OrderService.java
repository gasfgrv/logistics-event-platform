package com.gasfgrv.logistics.order.domain.services;

import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.domain.ports.out.OrderPort;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.gasfgrv.logistics.order.domain.models.enums.OrderStatus.CANCELLED;
import static com.gasfgrv.logistics.order.domain.models.enums.OrderStatus.CREATED;

@RequiredArgsConstructor
public class OrderService {

    private final Clock clock;
    private final OrderPort orders;

    public Order createNewOrder(Order orderData) {
        return Order.builder()
                .id(UUID.randomUUID())
                .customerId(orderData.getCustomerId())
                .origin(orderData.getOrigin())
                .destination(orderData.getDestination())
                .weight(orderData.getWeight())
                .status(CREATED)
                .createdAt(LocalDateTime.now(clock))
                .build();
    }

    public void cancelOrder(Order order) {
        order.setStatus(CANCELLED);
    }

    public void saveOrder(Order order) {
        orders.save(order);
    }

    public Optional<Order> getOrder(UUID orderId) {
        return orders.getOrderInfo(orderId);
    }

    public void sendToFreight(Order order) {
        orders.sendOrder(order);
    }

    public void notifyCancellationOfTheOrder(Order order, String reason) {
        orders.notifyCancellation(order, reason);
    }

}
