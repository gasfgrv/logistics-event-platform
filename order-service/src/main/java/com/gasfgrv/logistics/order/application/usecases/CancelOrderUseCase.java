package com.gasfgrv.logistics.order.application.usecases;

import com.gasfgrv.logistics.order.domain.commands.CancelOrderCommand;
import com.gasfgrv.logistics.order.domain.exceptions.NonExistingOrderException;
import com.gasfgrv.logistics.order.domain.exceptions.OrderAlreadyBeenCancelled;
import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.domain.ports.in.UsecasePort;
import com.gasfgrv.logistics.order.domain.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelOrderUseCase implements UsecasePort<CancelOrderCommand> {

    private final OrderService orderService;

    @Override
    public void execute(CancelOrderCommand command) {
        log.info("Canceling the order {}", command.getOrderId());
        Optional<Order> existingOrder = orderService.getOrder(command.getOrderId());

        if (existingOrder.isEmpty()) {
            log.error("The order could not be found");
            throw new NonExistingOrderException();
        }

        Order orderToBeCanceled = existingOrder.get();

        if (OrderStatus.CANCELLED.equals(orderToBeCanceled.getStatus())) {
            log.error("This order has already been cancelled");
            throw new OrderAlreadyBeenCancelled();
        }

        orderService.cancelOrder(orderToBeCanceled);

        log.info("Saving changes to the order {}", command.getOrderId());
        orderService.saveOrder(orderToBeCanceled);

        log.info("Notifying of the order cancellation");
        orderService.notifyCancellationOfTheOrder(orderToBeCanceled, command.reason());
    }

}
