package com.gasfgrv.logistics.freight.application;

import com.gasfgrv.logistics.freight.application.exceptions.ApplicationException;
import com.gasfgrv.logistics.freight.domain.exceptions.CalculateFreightException;
import com.gasfgrv.logistics.freight.domain.models.order.Order;
import com.gasfgrv.logistics.freight.domain.ports.in.CalculateOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.domain.ports.out.CalculateFreightPort;
import com.gasfgrv.logistics.freight.domain.ports.out.FreightRepositoryPort;
import com.gasfgrv.logistics.freight.domain.ports.out.NotifyFailurePort;
import com.gasfgrv.logistics.freight.domain.ports.out.SendToTransportPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CalculateOrderFreightUsecase implements CalculateOrderFreightUsecasePort {

    private final CalculateFreightPort calculateFreightClient;
    private final FreightRepositoryPort repository;
    private final SendToTransportPort transportNotifier;
    private final NotifyFailurePort notifyFailurePort;

    @Override
    public void calculate(Order order) {
        try {
            log.info("Calculating order freight");
            var freight = calculateFreightClient.calculateFreight(order);
            var savedFreight = repository.saveFreight(freight);

            log.info("Sending order freight to transport");
            transportNotifier.sendFreightToTransport(savedFreight);
        } catch (CalculateFreightException e) {
            log.error("Error while sending order freight to transport", e);
            notifyFailurePort.notifyFailure(order, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("Error while sending order freight to transport", e);
            throw new ApplicationException("Error while attempting to calculate freight", e);
        }
    }

}
