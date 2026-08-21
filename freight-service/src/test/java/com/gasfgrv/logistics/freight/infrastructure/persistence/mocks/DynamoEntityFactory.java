package com.gasfgrv.logistics.freight.infrastructure.persistence.mocks;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.infrastructure.persistence.entities.FreightEntity;

public class DynamoEntityFactory {

    public static FreightEntity buildEntity(Freight freight) {
        var entity = new FreightEntity();
        entity.setId(freight.getId());
        entity.setOrder(freight.getOrder().getId());
        entity.setDistanceKm(freight.getDistanceKm());
        entity.setPrice(freight.getPrice());
        entity.setStatus(freight.getStatus());
        entity.setCreatedAt(freight.getCreatedAt());
        return entity;
    }

}
