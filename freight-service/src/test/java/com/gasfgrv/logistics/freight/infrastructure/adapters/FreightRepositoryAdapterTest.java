package com.gasfgrv.logistics.freight.infrastructure.adapters;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.domain.models.mocks.EntityMockFactory;
import com.gasfgrv.logistics.freight.infrastructure.mappers.FreightMapperImpl;
import com.gasfgrv.logistics.freight.infrastructure.persistence.entities.FreightEntity;
import com.gasfgrv.logistics.freight.infrastructure.persistence.mocks.DynamoEntityFactory;
import com.gasfgrv.logistics.freight.infrastructure.persistence.repositories.DynamoDbTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {FreightRepositoryAdapter.class, FreightMapperImpl.class})
class FreightRepositoryAdapterTest {

    @Captor
    private ArgumentCaptor<FreightEntity> captor;

    @MockitoSpyBean
    private FreightMapperImpl mapper;

    @MockitoBean
    private DynamoDbTableRepository repository;

    @Autowired
    private FreightRepositoryAdapter adapter;

    private Freight freight;
    private FreightEntity freightEntity;

    @BeforeEach
    void setUp() {
        this.freight = EntityMockFactory.buildFreight(null);
        this.freightEntity = DynamoEntityFactory.buildEntity(freight);
    }

    @Test
    void shouldSaveFreight() {
        doReturn(freightEntity).when(repository).save(captor.capture());

        Freight saved = adapter.saveFreight(freight);

        assertNotNull(saved);
        assertTrue(new ReflectionEquals(freight, "order").matches(saved));
        assertEquals(freight.getOrder().getId(), saved.getOrder().getId());

        FreightEntity value = captor.getValue();
        assertTrue(new ReflectionEquals(freightEntity).matches(value));

        verify(mapper).toDomain(freightEntity);
        verify(mapper).toEntity(freight);
        verify(repository).save(value);
    }

    @Test
    void shouldReturnFalseWhenFreightHasAlreadyBeenCalculatedForTheOrderIsEmpty() {
        doReturn(Optional.empty()).when(repository).findBySortKey(any(UUID.class));

        boolean result = adapter.freightHasAlreadyBeenCalculatedForTheOrder(UUID.randomUUID());

        assertFalse(result);

        verify(repository).findBySortKey(any(UUID.class));
    }

    @Test
    void shouldReturnTrueWhenFreightHasAlreadyBeenCalculatedForTheOrderIsPresent() {
        doReturn(Optional.of(freightEntity)).when(repository).findBySortKey(any(UUID.class));

        boolean result = adapter.freightHasAlreadyBeenCalculatedForTheOrder(UUID.randomUUID());

        assertTrue(result);

        verify(repository).findBySortKey(any(UUID.class));
    }

    @Test
    void shouldReturnEmptyWhenNotFoundByOrderId() {
        doReturn(Optional.empty()).when(repository).findBySortKey(any(UUID.class));

        Optional<Freight> result = adapter.getFreightByOrder(UUID.randomUUID());

        assertFalse(result.isPresent());

        verify(repository).findBySortKey(any(UUID.class));
        verify(mapper, never()).toDomain(freightEntity);
    }

    @Test
    void shouldReturnFreightWhenFoundByOrderId() {
        doReturn(Optional.of(freightEntity)).when(repository).findBySortKey(any(UUID.class));

        Optional<Freight> result = adapter.getFreightByOrder(UUID.randomUUID());

        assertTrue(result.isPresent());
        assertTrue(new ReflectionEquals(freight, "order").matches(result.get()));
        assertEquals(freight.getOrder().getId(), result.get().getOrder().getId());

        verify(repository).findBySortKey(any(UUID.class));
        verify(mapper).toDomain(freightEntity);
    }

}
