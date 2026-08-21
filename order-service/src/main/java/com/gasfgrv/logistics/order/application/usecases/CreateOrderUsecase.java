package com.gasfgrv.logistics.order.application.usecases;

import com.gasfgrv.logistics.order.domain.commands.CreateOrderCommand;
import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.domain.ports.in.UsecasePort;
import com.gasfgrv.logistics.order.domain.services.AddressService;
import com.gasfgrv.logistics.order.domain.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderUsecase implements UsecasePort<CreateOrderCommand> {

    private final OrderService orderService;
    private final AddressService addressService;

    @Override
    public void execute(CreateOrderCommand command) {
        log.info("Creating a new order");
        command.getOrderAddresses()
                .forEach(addressService::isValidAddress);

        var newOrder = orderService.createNewOrder(command.order());

        log.info("Saving order {} information", command.getOrderId());
        orderService.saveOrder(newOrder);

        log.info("Sending data for freight");
        orderService.sendToFreight(newOrder);
    }

}
