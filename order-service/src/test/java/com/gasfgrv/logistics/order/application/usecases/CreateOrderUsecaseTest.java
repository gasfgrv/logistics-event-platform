package com.gasfgrv.logistics.order.application.usecases;

import com.gasfgrv.logistics.order.domain.commands.CreateOrderCommand;
import com.gasfgrv.logistics.order.domain.exceptions.InvalidAddressException;
import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.domain.services.AddressService;
import com.gasfgrv.logistics.order.domain.services.OrderService;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateOrderUsecaseTest {

    @Mock
    private OrderService orderService;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private CreateOrderUsecase createOrderUsecase;

    @Test
    @DisplayName("UseCase should successfully create a new order")
    void usecaseSholdSuccessfullyCreateANewOrder() {
        // Arrange
        var order = instantiateOrder();
        var command = instantiateCommand(order);

        Order orderCreated = Instancio.of(Order.class)
                .set(Select.field(Order::getId), order.getId())
                .set(Select.field(Order::getCustomerId), order.getCustomerId())
                .set(Select.field(Order::getOrigin), order.getOrigin())
                .set(Select.field(Order::getDestination), order.getDestination())
                .set(Select.field(Order::getWeight), order.getWeight())
                .set(Select.field(Order::getStatus), OrderStatus.CREATED)
                .generate(Select.field(Order::getCreatedAt), generator -> generator.temporal().localDateTime().past())
                .create();

        doNothing().when(addressService).isValidAddress(anyString());

        doReturn(orderCreated).when(orderService).createNewOrder(command.order());

        doNothing().when(orderService).saveOrder(any(Order.class));
        doNothing().when(orderService).sendToFreight(any(Order.class));

        // Act
        createOrderUsecase.execute(command);

        // Assert
        verify(addressService, times(command.getOrderAddresses().size())).isValidAddress(anyString());

        verify(orderService).createNewOrder(command.order());

        var orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderService).saveOrder(orderCaptor.capture());

        var captured = orderCaptor.getValue();
        assertThat(captured).isNotNull();
        assertThat(captured.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(captured.getCreatedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("UseCase should throw an InvalidAddressException when there is an invalid address")
    void usecaseSholdThrowAnInvalidAddressExceptionWhenThereIsAnInvalidAddress() {
        // Arrange
        var order = instantiateOrder();
        var command = instantiateCommand(order);

        doThrow(new InvalidAddressException())
                .when(addressService).isValidAddress(anyString());

        // Assert
        assertThatExceptionOfType(InvalidAddressException.class)
                .isThrownBy(() -> createOrderUsecase.execute(command))
                .withMessageContaining("Invalid address in origin or destination");

        verify(orderService, never()).createNewOrder(any(Order.class));
        verify(orderService, never()).saveOrder(any(Order.class));
        verify(orderService, never()).sendToFreight(any(Order.class));
    }

    private static Order instantiateOrder() {
        return Instancio.of(Order.class)
                .set(Select.field(Order::getStatus), null)
                .set(Select.field(Order::getCreatedAt), null)
                .generate(Select.field(Order::getOrigin), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(Order::getDestination), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(Order::getWeight), gen -> gen.floats().range(1f, 100f))
                .create();
    }

    private static CreateOrderCommand instantiateCommand(Order order) {
        return Instancio.of(CreateOrderCommand.class)
                .set(Select.field(CreateOrderCommand::order), order)
                .create();
    }

}