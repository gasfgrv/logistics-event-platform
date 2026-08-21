package com.gasfgrv.logistics.order.application.usecases;

import com.gasfgrv.logistics.order.domain.commands.CancelOrderCommand;
import com.gasfgrv.logistics.order.domain.exceptions.NonExistingOrderException;
import com.gasfgrv.logistics.order.domain.exceptions.OrderAlreadyBeenCancelled;
import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.domain.services.OrderService;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelOrderUseCaseTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CancelOrderUseCase cancelOrderUseCase;

    @Test
    @DisplayName("Usecase must successfully cancel an order")
    void usecaseMustSuccessfullyCancelAnOrder() {
        // Arrange
        var order = instantiateOrder();
        var command = instantiateCommand(order);

        var orderFound = instatiateServiceReturn(order.getId(), OrderStatus.CREATED);

        doReturn(Optional.of(orderFound))
                .when(orderService).getOrder(command.getOrderId());

        doNothing().when(orderService).cancelOrder(orderFound);
        doNothing().when(orderService).saveOrder(orderFound);
        doNothing().when(orderService).notifyCancellationOfTheOrder(orderFound, command.reason());

        // Assert
        assertThatCode(() -> cancelOrderUseCase.execute(command))
                .doesNotThrowAnyException();

        verify(orderService).getOrder(any(UUID.class));
        verify(orderService).cancelOrder(any(Order.class));
        verify(orderService).notifyCancellationOfTheOrder(any(Order.class), anyString());
    }

    @Test
    @DisplayName("Usecase should throw a NonExistingOrderException when it cannot find the order")
    void usecaseShouldThrowANonExistingOrderExceptionWhenItCannotFindTheOrder() {
        // Arrange
        var order = instantiateOrder();
        var command = instantiateCommand(order);

        doReturn(Optional.empty())
                .when(orderService).getOrder(command.getOrderId());

        // Assert
        assertThatExceptionOfType(NonExistingOrderException.class)
                .isThrownBy(() -> cancelOrderUseCase.execute(command))
                .withMessageContaining("Non-existing order");

        verify(orderService, never()).cancelOrder(any(Order.class));
        verify(orderService, never()).notifyCancellationOfTheOrder(any(Order.class), anyString());
    }

    @Test
    @DisplayName("Usecase should throw a OrderAlreadyBeenCancelled when order has already been cancelled")
    void usecaseShouldThrowAOrderAlreadyBeenCancelledWhenOrderHasAlreadyBeenCancelled() {
        // Arrange
        var order = instantiateOrder();
        var command = instantiateCommand(order);
        var orderFound = instatiateServiceReturn(order.getId(), OrderStatus.CANCELLED);

        doReturn(Optional.of(orderFound))
                .when(orderService).getOrder(command.getOrderId());

        // Assert
        assertThatExceptionOfType(OrderAlreadyBeenCancelled.class)
                .isThrownBy(() -> cancelOrderUseCase.execute(command))
                .withMessageContaining("This order has already been cancelled");

        verify(orderService, never()).cancelOrder(any(Order.class));
        verify(orderService, never()).notifyCancellationOfTheOrder(any(Order.class), anyString());

    }

    private Order instatiateServiceReturn(UUID id, OrderStatus orderStatus) {
        return Instancio.of(Order.class)
                .set(Select.field(Order::getId), id)
                .set(Select.field(Order::getStatus), orderStatus)
                .create();
    }


    private static Order instantiateOrder() {
        return Instancio.of(Order.class)
                .set(Select.field(Order::getCustomerId), null)
                .set(Select.field(Order::getOrigin), null)
                .set(Select.field(Order::getDestination), null)
                .set(Select.field(Order::getWeight), 0f)
                .set(Select.field(Order::getStatus), null)
                .set(Select.field(Order::getCreatedAt), null)
                .create();
    }

    private static CancelOrderCommand instantiateCommand(Order order) {
        return Instancio.of(CancelOrderCommand.class)
                .set(Select.field(CancelOrderCommand::order), order)
                .set(Select.field(CancelOrderCommand::reason), "Testing")
                .create();
    }

}