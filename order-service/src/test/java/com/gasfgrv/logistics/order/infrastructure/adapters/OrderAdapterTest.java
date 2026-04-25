package com.gasfgrv.logistics.order.infrastructure.adapters;

import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.entities.OrdersEntity;
import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.repositories.OrderRepository;
import com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers.OrderCancelledProducer;
import com.gasfgrv.logistics.order.infrastructure.integrations.kafka.producers.OrderCreatedProducer;
import com.gasfgrv.logistics.order.infrastructure.mappers.OrderMapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrderAdapterTest {

    @Captor
    ArgumentCaptor<Order> orderCaptor;

    @Captor
    ArgumentCaptor<OrdersEntity> ordersEntityCaptor;

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private OrderCreatedProducer orderCreatedProducer;

    @Mock
    private OrderCancelledProducer orderCancelledProducer;

    @InjectMocks
    private OrderAdapter adapter;

    @Test
    @DisplayName("OrderAdapter should save an order")
    void orderadapterShouldSaveAnOrder() {
        // Arrange
        Order order = generateOrder();
        OrdersEntity entity = generateEntity();

        doReturn(entity).when(mapper).toEntity(order);
        doNothing().when(repository).save(entity);

        // Act
        adapter.save(order);

        // Assert
        verify(mapper, times(1)).toEntity(orderCaptor.capture());
        Order orderValue = orderCaptor.getValue();
        assertThat(orderValue).isEqualTo(order);

        verify(repository, times(1)).save(ordersEntityCaptor.capture());
        OrdersEntity ordersEntityValue = ordersEntityCaptor.getValue();
        assertThat(ordersEntityValue).isEqualTo(entity);
    }

    @Test
    @DisplayName("OrderAdapter should send the order for shipping")
    void orderAdapterShouldSendTheOrderForShipping() {
        // Arrange
        Order order = generateOrder();

        doNothing().when(orderCreatedProducer).sendMessage(order);

        // Act
        adapter.sendOrder(order);

        // Assert
        verify(orderCreatedProducer, times(1)).sendMessage(orderCaptor.capture());
        Order value = orderCaptor.getValue();
        assertThat(value).isEqualTo(order);

    }

    @Test
    @DisplayName("OrderAdapter notifies you of the order cancellation")
    void orderAdapterNotifiesYouOfTheOrderCancellation() {
        // Arrange
        Order order = generateOrder();
        String reason = Instancio.gen().text().word().get();

        doNothing().when(orderCancelledProducer).sendMessage(order, reason);

        // Act
        adapter.notifyCancellation(order, reason);

        // Assert
        verify(orderCancelledProducer, times(1)).sendMessage(orderCaptor.capture(), anyString());
        Order value = orderCaptor.getValue();
        assertThat(value).isEqualTo(order);
    }

    @Test
    @DisplayName("OrderAdapter should return an order if one exists")
    void orderAdapterShouldReturnAnOrderIfOneExists() {
        // Arrange
        UUID orderId = UUID.fromString(Instancio.gen().text().uuid().get());
        OrdersEntity entity = generateEntity();
        Order order = generateOrder();

        doReturn(Optional.of(entity)).when(repository).query(orderId);
        doReturn(order).when(mapper).toDomain(entity);

        // Act
        Optional<Order> orderInfo = adapter.getOrderInfo(orderId);

        // Assert
        verify(repository, times(1)).query(orderId);
        verify(mapper, times(1)).toDomain(entity);
        assertThat(orderInfo).isPresent();
    }

    @Test
    @DisplayName("OrderAdapter should return empty if there is no order")
    void orderAdapterShouldReturnEmptyIfThereIsNoOrder() {
        // Arrange
        UUID orderId = UUID.fromString(Instancio.gen().text().uuid().get());
        doReturn(Optional.empty()).when(repository).query(orderId);

        // Act
        Optional<Order> orderInfo = adapter.getOrderInfo(orderId);

        // Assert
        verify(repository, times(1)).query(orderId);
        verify(mapper, never()).toDomain(any(OrdersEntity.class));
        assertThat(orderInfo).isEmpty();
    }

    private OrdersEntity generateEntity() {
        return Instancio.create(OrdersEntity.class);
    }

    private static Order generateOrder() {
        return Instancio.create(Order.class);
    }

}