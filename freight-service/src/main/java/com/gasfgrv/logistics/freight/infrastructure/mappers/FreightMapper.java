package com.gasfgrv.logistics.freight.infrastructure.mappers;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.infrastructure.persistence.entities.FreightEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FreightMapper {

    @Mapping(source = "order.id", target = "order")
    FreightEntity toEntity(Freight freight);

    @Mapping(source = "order", target = "order.id")
    Freight toDomain(FreightEntity freightEntity);

}
