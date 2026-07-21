package com.gasfgrv.logistics.freight.infrastructure.persistence.repositories;

import com.gasfgrv.logistics.freight.infrastructure.persistence.entities.FreightEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DynamoDbTableRepository {

    private final DynamoDbTable<FreightEntity> table;

    public FreightEntity save(FreightEntity entity) {
        table.putItem(builder -> builder.item(entity));
        return entity;
    }

    public Optional<FreightEntity> findBySortKey(UUID orderId) {
        DynamoDbIndex<FreightEntity> index = table.index("order_id-index");
        Key key = Key.builder()
                .partitionValue(orderId.toString())
                .build();
        return index.query(QueryConditional.keyEqualTo(key))
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

}
