package com.gasfgrv.logistics.freight.infrastructure.mappers;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.infrastructure.persistence.entities.FreightEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FreightMapper {

    FreightEntity toEntity(Freight freight);

    Freight toDomain(FreightEntity freightEntity);

}
