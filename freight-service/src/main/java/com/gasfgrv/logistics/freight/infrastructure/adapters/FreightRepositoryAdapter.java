package com.gasfgrv.logistics.freight.infrastructure.adapters;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.domain.ports.out.FreightRepositoryPort;
import com.gasfgrv.logistics.freight.infrastructure.mappers.FreightMapper;
import com.gasfgrv.logistics.freight.infrastructure.persistence.entities.FreightEntity;
import com.gasfgrv.logistics.freight.infrastructure.persistence.repositories.DynamoDbTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FreightRepositoryAdapter implements FreightRepositoryPort {

    private final FreightMapper mapper;
    private final DynamoDbTableRepository repository;

    @Override
    public Freight saveFreight(Freight freight) {
        FreightEntity saved = repository.save(mapper.toEntity(freight));
        return mapper.toDomain(saved);
    }

    @Override
    public boolean freightHasAlreadyBeenCalculatedForTheOrder(UUID orderId) {
        return repository.findBySortKey(orderId).isPresent();
    }

    @Override
    public Optional<Freight> getFreightByOrder(UUID orderId) {
        return repository.findBySortKey(orderId).map(mapper::toDomain);
    }

}
