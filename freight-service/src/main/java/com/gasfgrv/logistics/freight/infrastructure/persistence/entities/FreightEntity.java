package com.gasfgrv.logistics.freight.infrastructure.persistence.entities;

import com.gasfgrv.logistics.freight.domain.models.enuns.FreightStatus;
import com.gasfgrv.logistics.freight.infrastructure.persistence.converters.LocalDateTimeAttributeConverter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Setter
@DynamoDbBean
public class FreightEntity {

    private UUID id;
    private UUID order;
    private double distanceKm;
    private BigDecimal price;
    private FreightStatus status;
    private Instant createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("freight_id")
    public UUID getId() {
        return id;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("order_id")
    public UUID getOrder() {
        return order;
    }

    @DynamoDbAttribute("distance_km")
    public double getDistanceKm() {
        return distanceKm;
    }

    @DynamoDbAttribute("price")
    public BigDecimal getPrice() {
        return price;
    }

    @DynamoDbAttribute("status")
    public FreightStatus getStatus() {
        return status;
    }

    @DynamoDbAttribute("created_at")
    @DynamoDbConvertedBy(LocalDateTimeAttributeConverter.class)
    public Instant getCreatedAt() {
        return createdAt;
    }
}
