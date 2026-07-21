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

import java.util.Optional;
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
        FreightEntity entity = mapper.toEntity(EntityMockFactory.buildFreight(null));

        FreightEntity saved = repository.save(entity);

        assertSame(entity, saved);

        Consumer<Key.Builder> keyConsumer = key -> key.partitionValue(entity.getId().toString())
                .sortValue(entity.getOrder().toString());
        FreightEntity item = table.getItem(builder -> builder.key(keyConsumer));
        assertNotNull(item);
        assertTrue(new ReflectionEquals(saved).matches(item));
    }

    @Test
    void shouldFindEntityBySortKeyWhenPresent() {
        FreightEntity entity = mapper.toEntity(EntityMockFactory.buildFreight(null));
        table.putItem(builder -> builder.item(entity));

        Optional<FreightEntity> freight = repository.findBySortKey(entity.getOrder());

        assertTrue(freight.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalWhenEntityNotFound() {
        Optional<FreightEntity> freight = repository.findBySortKey(UUID.randomUUID());

        assertTrue(freight.isEmpty());
    }

}