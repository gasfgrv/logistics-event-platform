package com.gasfgrv.logistics.freight.infrastructure.persistence.repositories;

import com.gasfgrv.logistics.freight.domain.models.mocks.EntityMockFactory;
import com.gasfgrv.logistics.freight.infrastructure.configurations.DynamoDbConfiguration;
import com.gasfgrv.logistics.freight.infrastructure.configurations.DynamoDbProperties;
import com.gasfgrv.logistics.freight.infrastructure.configurations.containers.LocalstackTestcontainersConfiguration;
import com.gasfgrv.logistics.freight.infrastructure.mappers.FreightMapperImpl;
import com.gasfgrv.logistics.freight.infrastructure.persistence.entities.FreightEntity;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Import(LocalstackTestcontainersConfiguration.class)
@EnableConfigurationProperties(DynamoDbProperties.class)
@SpringBootTest(classes = {DynamoDbTableRepository.class, DynamoDbConfiguration.class, FreightMapperImpl.class})
class DynamoDbTableRepositoryTest {

    @Autowired
    private DynamoDbTableRepository repository;

    @Autowired
    private DynamoDbTable<FreightEntity> table;

    @Autowired
    private FreightMapperImpl mapper;

    @Test
    void shouldSaveEntity() {
        var entity = mapper.toEntity(EntityMockFactory.buildFreight(null));

        var saved = repository.save(entity);

        assertSame(entity, saved);

        var keyConsumer = getKeyConsumer(entity);
        var item = table.getItem(builder -> builder.key(keyConsumer));
        assertNotNull(item);
        assertTrue(new ReflectionEquals(saved).matches(item));
    }

    @Test
    void shouldFindEntityBySortKeyWhenPresent() {
        var entity = mapper.toEntity(EntityMockFactory.buildFreight(null));
        table.putItem(builder -> builder.item(entity));

        var freight = repository.findBySortKey(entity.getOrder());

        assertTrue(freight.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalWhenEntityNotFound() {
        var freight = repository.findBySortKey(UUID.randomUUID());

        assertTrue(freight.isEmpty());
    }

    private Consumer<Key.Builder> getKeyConsumer(FreightEntity entity) {
        return key -> key
                .partitionValue(entity.getId().toString())
                .sortValue(entity.getOrder().toString());
    }

}
