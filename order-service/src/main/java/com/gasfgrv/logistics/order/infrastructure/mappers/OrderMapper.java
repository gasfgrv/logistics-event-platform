package com.gasfgrv.logistics.order.infrastructure.mappers;

import com.gasfgrv.logistics.order.domain.commands.CancelOrderCommand;
import com.gasfgrv.logistics.order.domain.commands.CreateOrderCommand;
import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import com.gasfgrv.logistics.order.domain.models.order.Order;
import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.entities.OrdersEntity;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.CancelOrderDto;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.CreateOrderDto;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.OrderResponseDto;
import com.logistics.order.avro.v1.OrderCanceledEvent;
import com.logistics.order.avro.v1.OrderCreatedEvent;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToOrderStatus")
    Order toDomain(OrdersEntity entity);

    @Mapping(target = "status", source = "status", qualifiedByName = "orderStatusToString")
    OrdersEntity toEntity(Order order);

    @Mapping(target = "orderId", source = "id", qualifiedByName = "uuidToCharSequence")
    @Mapping(target = "customerId", source = "customerId", qualifiedByName = "uuidToCharSequence")
    @Mapping(target = "weight", source = "weight", qualifiedByName = "roundToTwo")
    OrderCreatedEvent toCreatedEvent(Order order);

    @Mapping(target = "orderId", source = "order.id", qualifiedByName = "uuidToCharSequence")
    @Mapping(target = "reason", source = "reason")
    OrderCanceledEvent toCanceledEvent(Order order, String reason);

    @Mapping(target = "order.customerId", source = "customerId")
    @Mapping(target = "order.origin", source = "origin")
    @Mapping(target = "order.destination", source = "destination")
    @Mapping(target = "order.weight", source = "weight")
    @Mapping(target = "order.id", ignore = true)
    @Mapping(target = "order.status", ignore = true)
    @Mapping(target = "order.createdAt", ignore = true)
    CreateOrderCommand toCreateCommand(CreateOrderDto dto);

    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "order.status", ignore = true)
    @Mapping(target = "order.createdAt", ignore = true)
    @Mapping(target = "order.customerId", ignore = true)
    @Mapping(target = "order.origin", ignore = true)
    @Mapping(target = "order.destination", ignore = true)
    @Mapping(target = "order.weight", ignore = true)
    CancelOrderCommand toCancelCommand(CancelOrderDto dto);

    OrderResponseDto toResponse(CreateOrderDto dto);

    @Named("orderStatusToString")
    default String orderStatusToString(OrderStatus status) {
        return status != null ? status.getStatus() : null;
    }

    @Named("stringToOrderStatus")
    default OrderStatus stringToOrderStatus(String status) {
        return status != null ? OrderStatus.fromValue(status) : null;
    }

    @Named("uuidToCharSequence")
    default CharSequence uuidToCharSequence(UUID id) {
        return id != null ? id.toString() : null;
    }

    @Named("roundToTwo")
    default double roundToTwo(float value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
