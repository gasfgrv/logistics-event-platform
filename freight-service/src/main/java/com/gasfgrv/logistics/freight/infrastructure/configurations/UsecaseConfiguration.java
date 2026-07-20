package com.gasfgrv.logistics.freight.infrastructure.configurations;

import com.gasfgrv.logistics.freight.application.CalculateOrderFreightUsecase;
import com.gasfgrv.logistics.freight.application.CancellOrderFreightUsecase;
import com.gasfgrv.logistics.freight.domain.ports.in.CalculateOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.domain.ports.in.CancellOrderFreightUsecasePort;
import com.gasfgrv.logistics.freight.domain.ports.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsecaseConfiguration {

    @Bean
    public CalculateOrderFreightUsecasePort calculateOrderFreightUsecasePort(CalculateFreightPort calculateFreightClient,
                                                                             FreightRepositoryPort repository,
                                                                             SendToTransportPort transportNotifier,
                                                                             NotifyFailurePort notifyFailurePort) {
        return new CalculateOrderFreightUsecase(calculateFreightClient,
                repository,
                transportNotifier,
                notifyFailurePort);
    }


    @Bean
    public CancellOrderFreightUsecasePort cancellOrderFreightUsecasePort(FreightRepositoryPort repository,
                                                                         NotifyCancellationPort notifier) {
        return new CancellOrderFreightUsecase(repository, notifier);
    }

}
