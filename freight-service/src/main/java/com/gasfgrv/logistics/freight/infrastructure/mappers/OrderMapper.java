package com.gasfgrv.logistics.freight.infrastructure.mappers;

import com.gasfgrv.logistics.freight.domain.models.order.Order;
import com.logistics.order.avro.v1.OrderCanceledEvent;
import com.logistics.order.avro.v1.OrderCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", source = "orderId", qualifiedByName = "toUuid")
    @Mapping(target = "customerId", source = "customerId", qualifiedByName = "toUuid")
    @Mapping(target = "origin", source = "origin", qualifiedByName = "toString")
    @Mapping(target = "destination", source = "destination", qualifiedByName = "toString")
    @Mapping(target = "weight", source = "weight", qualifiedByName = "toFloat")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    Order toDomain(OrderCreatedEvent payload);

    @Mapping(target = "id", source = "orderId", qualifiedByName = "toUuid")
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "weight", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", constant = "CANCELLED")
    Order toDomain(OrderCanceledEvent payload);

    @Named("toUuid")
    default UUID toUuid(CharSequence value) {
        return value != null ? UUID.fromString(value.toString()) : null;
    }

    @Named("toFloat")
    default Float toFloat(Double value) {
        return value != null ? value.floatValue() : null;
    }

    @Named("toString")
    default String toString(CharSequence value) {
        return value != null ? value.toString() : null;
    }

}
