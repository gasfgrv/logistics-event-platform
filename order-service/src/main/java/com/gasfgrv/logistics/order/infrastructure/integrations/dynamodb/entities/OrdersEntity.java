package com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.entities;

import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.converters.LocalDateTimeAttributeConverter;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDbBean
@Setter
@ToString
public class OrdersEntity {

    private UUID id;
    private UUID customerId;
    private String origin;
    private String destination;
    private float weight;
    private String status;
    private LocalDateTime createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("order_id")
    public UUID getId() {
        return id;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("order_customer")
    public UUID getCustomerId() {
        return customerId;
    }

    @DynamoDbAttribute("order_origin")
    public String getOrigin() {
        return origin;
    }

    @DynamoDbAttribute("order_destination")
    public String getDestination() {
        return destination;
    }

    @DynamoDbAttribute("order_weight")
    public float getWeight() {
        return weight;
    }

    @DynamoDbAttribute("order_status")
    public String getStatus() {
        return status;
    }

    @DynamoDbConvertedBy(LocalDateTimeAttributeConverter.class)
    @DynamoDbAttribute("order_creation")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
