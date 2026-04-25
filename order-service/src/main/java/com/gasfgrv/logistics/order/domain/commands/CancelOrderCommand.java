package com.gasfgrv.logistics.order.domain.commands;

import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.domain.ports.in.UsecaseCommand;

import java.util.UUID;

public record CancelOrderCommand(Order order, String reason) implements UsecaseCommand {

    public UUID getOrderId() {
        return order.getId();
    }

}
