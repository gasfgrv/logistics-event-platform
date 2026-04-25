package com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.repositories;

import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.entities.OrdersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepository {

    private final DynamoDbTable<OrdersEntity> dynamoDbTable;

    public void save(OrdersEntity entity) {
        dynamoDbTable.putItem(builder -> builder.item(entity));
    }

    public Optional<OrdersEntity> query(UUID orderId) {
        return dynamoDbTable.query(query -> query.queryConditional(setQueryConditional(orderId)))
                .items()
                .stream()
                .findFirst();
    }

    private static QueryConditional setQueryConditional(UUID orderId) {
        return QueryConditional.keyEqualTo(key -> key.addPartitionValue(orderId));
    }

}
