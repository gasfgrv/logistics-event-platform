package com.gasfgrv.logistics.order.infrastructure.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

import java.util.UUID;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gasfgrv.logistics.order.domain.commands.CancelOrderCommand;
import com.gasfgrv.logistics.order.domain.commands.CreateOrderCommand;
import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.CancelOrderDto;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.CreateOrderDto;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.OrderResponseDto;
import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.entities.OrdersEntity;
import com.logistics.order.avro.v1.OrderCanceledEvent;
import com.logistics.order.avro.v1.OrderCreatedEvent;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(OrderMapper.class);
    }

    @Test
    @DisplayName("OrderMapper should map OrdersEntity to Order")
    void orderMapperShouldMapOrdersEntityToOrder() {
        // Arrange
        var entity = generateEntity();

        // Act
        var domain = mapper.toDomain(entity);

        // Assert
        assertThat(domain).isNotNull()
                .isInstanceOf(Order.class)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("status")
                .isEqualTo(entity);

        assertThat(domain.getStatus())
                .hasFieldOrPropertyWithValue("status", entity.getStatus());
    }

    @Test
    @DisplayName("OrderMapper should map Order to OrdersEntity")
    void orderMapperShouldMapOrderToOrdersEntity() {
        // Arrange
        var domain = generateDomain();

        // Act
        var entity = mapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull()
                .isInstanceOf(OrdersEntity.class)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("status")
                .isEqualTo(domain);

        assertThat(domain.getStatus())
                .hasFieldOrPropertyWithValue("status", entity.getStatus());
    }

    @Test
    @DisplayName("OrderMapper should map Order to OrderCreatedEvent")
    void orderMapperShouldMapOrderToOrderCreatedEvent() {
        // Arrange
        var domain = generateDomain();

        // Act
        var event = mapper.toCreatedEvent(domain);

        // Assert
        assertThat(event).isNotNull()
                .isInstanceOf(OrderCreatedEvent.class)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("orderId", "customerId", "weight")
                .isEqualTo(domain);

        assertThat(event.getOrderId()).isEqualTo(domain.getId().toString());
        assertThat(event.getCustomerId()).isEqualTo(domain.getCustomerId().toString());

        var boxedWeight = Double.valueOf(event.getWeight());
        assertThat(boxedWeight.floatValue()).isEqualTo(domain.getWeight(), withPrecision(0.001f));
    }

    @Test
    @DisplayName("OrderMapper should map Order to OrderCanceledEvent")
    void orderMapperShouldMapOrderToOrderCanceledEvent() {
        // Arrange
        var domain = generateDomain();
        var reason = Instancio.gen().text().word().verb().get();

        // Act
        var event = mapper.toCanceledEvent(domain, reason);

        // Assert
        assertThat(event).isNotNull()
                .isInstanceOf(OrderCanceledEvent.class)
                .hasNoNullFieldsOrProperties();

        assertThat(event.getOrderId()).isEqualTo(domain.getId().toString());
        assertThat(event.getReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("OrderMapper should map CreateOrderDto to CreateOrderCommand")
    void orderMapperShouldMapCreateOrderDtoToCreateOrderCommand() {
        // Arrange
        var dto = (CreateOrderDto) generateDto(DtoType.CREATE);

        // Act
        var command = mapper.toCreateCommand(dto);

        // Assert
        assertThat(command.order()).isNotNull()
                .isInstanceOf(Order.class)
                .hasNoNullFieldsOrPropertiesExcept("id", "status", "createdAt")
                .usingRecursiveComparison()
                .comparingOnlyFields("customerId", "origin", "destination", "weight")
                .isEqualTo(dto);
    }

    @Test
    @DisplayName("OrderMapper should map CancelOrderDto to CancelOrderCommand")
    void orderMapperShouldMapCancelOrderDtoToCancelOrderCommand() {
        // Arrange
        var dto = (CancelOrderDto) generateDto(DtoType.CANCEL);

        // Act
        var command = mapper.toCancelCommand(dto);

        // Assert
        assertThat(command).isNotNull()
                .isInstanceOf(CancelOrderCommand.class)
                .hasNoNullFieldsOrProperties();

        assertThat(command.reason()).isNotBlank();

        assertThat(command.getOrderId()).isEqualTo(dto.orderId());
    }

    @Test
    @DisplayName("OrderMapper should map CreateOrderDto to OrderResponseDto")
    void orderMapperShouldMapCreateOrderDtoToOrderResponseDto() {
        // Arranje
        var dto = (CreateOrderDto) generateDto(DtoType.CREATE);

        // Act
        var response = mapper.toResponse(dto);

        // Assert
        assertThat(response).isNotNull()
                .isInstanceOf(OrderResponseDto.class)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(dto);
    }

    private static Order generateDomain() {
        return Instancio.of(Order.class)
                .set(Select.field(Order::getId), UUID.randomUUID())
                .set(Select.field(Order::getCustomerId), UUID.randomUUID())
                .generate(Select.field(Order::getOrigin), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(Order::getDestination), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(Order::getWeight), gen -> gen.floats().range(1f, 100f))
                .generate(Select.field(Order::getStatus), gen -> gen.enumOf(OrderStatus.class))
                .generate(Select.field(Order::getCreatedAt), gen -> gen.temporal().localDateTime().past())
                .create();
    }

    private static OrdersEntity generateEntity() {
        return Instancio.of(OrdersEntity.class)
                .set(Select.field(OrdersEntity::getId), UUID.randomUUID())
                .set(Select.field(OrdersEntity::getCustomerId), UUID.randomUUID())
                .generate(Select.field(OrdersEntity::getOrigin), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(OrdersEntity::getDestination), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(OrdersEntity::getWeight), gen -> gen.floats().range(1f, 100f))
                .supply(Select.field(OrdersEntity::getStatus), status -> status.oneOf(OrderStatus.values()).getStatus())
                .generate(Select.field(OrdersEntity::getCreatedAt), gen -> gen.temporal().localDateTime().past())
                .create();
    }

    private static Record generateDto(DtoType type) {
        return type.getDto();
    }

}
