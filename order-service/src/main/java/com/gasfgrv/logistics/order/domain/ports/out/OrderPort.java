package com.gasfgrv.logistics.order.domain.ports.out;

import com.gasfgrv.logistics.order.domain.models.order.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderPort {
    void save(Order order);

    void sendOrder(Order order);

    Optional<Order> getOrderInfo(UUID orderId);

    void notifyCancellation(Order order, String reason);
}
