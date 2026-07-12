package com.gasfgrv.logistics.freight.domain.ports.out;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;

import java.util.Optional;
import java.util.UUID;

public interface FreightRepositoryPort {

    Freight saveFreight(Freight freight);

    boolean freightHasAlreadyBeenCalculatedForTheOrder(UUID orderId);

    Optional<Freight> getFreightByOrder(UUID orderId);

}
