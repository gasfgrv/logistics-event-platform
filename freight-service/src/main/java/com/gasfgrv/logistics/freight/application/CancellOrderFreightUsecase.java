package com.gasfgrv.logistics.freight.application;

import com.gasfgrv.logistics.freight.domain.exceptions.NoFreightACalculatedException;
import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.domain.models.order.Order;
import com.gasfgrv.logistics.freight.domain.ports.in.CancellOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.domain.ports.out.FreightRepositoryPort;
import com.gasfgrv.logistics.freight.domain.ports.out.NotifyCancellationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CancellOrderFreightUsecase implements CancellOrderFreightUsecasePort {

    private final FreightRepositoryPort repository;
    private final NotifyCancellationPort notifier;

    @Override
    public void cancelOrderFreight(Order order) {
        var hasFreightAlreadyCalculated = repository.freightHasAlreadyBeenCalculatedForTheOrder(order.getId());

        if (!hasFreightAlreadyCalculated) {
            log.error("Cannot cancel order freight: no freight has been calculated for order {}", order.getId());
            throw new NoFreightACalculatedException("No freight has been calculated for the specified order");
        }

        log.info("Canceling order freight for order {}", order.getId());
        repository.getFreightByOrder(order.getId())
                .ifPresent(this::cancelAndNotify);
    }

    private void cancelAndNotify(Freight freight) {
        freight.setAsCancelled();
        notifier.notifyCancellation(freight);
    }

}
