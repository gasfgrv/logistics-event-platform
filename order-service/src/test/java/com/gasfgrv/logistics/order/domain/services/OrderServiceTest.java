package com.gasfgrv.logistics.order.domain.services;

import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.domain.ports.out.OrderPort;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static com.gasfgrv.logistics.order.domain.models.enums.OrderStatus.CANCELLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private Clock clock;

    @Mock
    private OrderPort orderPort;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("OrderService must create a new order")
    void orderServiceMustCreateNewOrder() {
        // Arrange
        Instant instant = Instant.parse("2026-03-04T10:00:00Z");
        String zipCodePattern = "#d#d#d#d#d-#d#d#d";

        doReturn(instant).when(clock).instant();
        doReturn(ZoneId.of("UTC")).when(clock).getZone();

        Order orderData = Instancio.of(Order.class)
                .set(Select.field(Order::getId), UUID.randomUUID())
                .set(Select.field(Order::getCustomerId), UUID.randomUUID())
                .generate(Select.field(Order::getOrigin), generator -> generator.text().pattern(zipCodePattern))
                .generate(Select.field(Order::getDestination), generator -> generator.text().pattern(zipCodePattern))
                .generate(Select.field(Order::getWeight), generator -> generator.floats().range(1f, 100f))
                .create();

        // Act
        Order order = orderService.createNewOrder(orderData);

        // Assert
        assertThat(order.getId())
                .matches(uuid -> uuid.toString().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
        assertThat(order.getCustomerId())
                .matches(uuid -> uuid.toString().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
        assertThat(order.getOrigin())
                .matches("\\d{5}-?\\d{3}");
        assertThat(order.getDestination())
                .matches("\\d{5}-?\\d{3}");
        assertThat(order.getWeight()).isBetween(1f, 100f);
        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.CREATED);
        assertThat(order.getCreatedAt())
                .hasYear(2026)
                .hasMonth(Month.MARCH)
                .hasDayOfMonth(4)
                .isInThePast();
    }

    @Test
    @DisplayName("OrderService must cancel the order")
    void orderServiceMustCancelTheOrder() {
        // Arrange
        Order order = new Order();

        // Act
        orderService.cancelOrder(order);

        // Assert
        assertThat(order.getStatus()).isEqualTo(CANCELLED);
    }

    @Test
    @DisplayName("OrderService must save the order")
    void orderServiceMustSaveTheOrder() {
        // Arrange
        Order order = new Order();

        // Act
        orderService.saveOrder(order);

        // Assert
        verify(orderPort).save(order);
    }

    @Test
    @DisplayName("OrderService must send the order to freight")
    void orderServiceMustSendTheOrderToFreight() {
        // Arrange
        Order order = new Order();

        // Act
        orderService.sendToFreight(order);

        // Assert
        verify(orderPort).sendOrder(order);
    }

    @Test
    @DisplayName("OrderService must notify the order cancelation")
    void orderServiceMustNotifyTheOrderCancelation() {
        // Arrange
        Order order = new Order();
        String reason = "Testing";

        // Act
        orderService.notifyCancellationOfTheOrder(order, reason);

        // Assert
        verify(orderPort).notifyCancellation(order, reason);
    }

    @Test
    @DisplayName("OrderService should return an order when queried")
    void orderServiceShouldReturnAnOrderWhenQueried() {
        // Arrange
        Order order = Instancio.of(Order.class)
                .set(Select.field(Order::getId), UUID.randomUUID())
                .create();

        doReturn(Optional.of(order)).when(orderPort).getOrderInfo(order.getId());

        // Act
        Optional<Order> orderOptional = orderService.getOrder(order.getId());

        // Assert
        assertThat(orderOptional).isNotEmpty();
        assertThat(orderOptional.get()).usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(order);

        verify(orderPort).getOrderInfo(any(UUID.class));
    }

    @Test
    @DisplayName("OrderService should return an empty object when queried")
    void orderServiceShouldReturnAnEmptyObjectWhenQueried() {
        // Arrange
        Order order = Instancio.of(Order.class)
                .set(Select.field(Order::getId), UUID.randomUUID())
                .create();

        doReturn(Optional.empty()).when(orderPort).getOrderInfo(order.getId());

        // Act
        Optional<Order> orderOptional = orderService.getOrder(order.getId());

        // Assert
        assertThat(orderOptional).isEmpty();
        verify(orderPort).getOrderInfo(any(UUID.class));
    }

}