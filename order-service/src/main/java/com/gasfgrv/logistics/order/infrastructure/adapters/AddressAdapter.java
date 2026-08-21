package com.gasfgrv.logistics.order.infrastructure.adapters;

import com.gasfgrv.logistics.order.domain.ports.out.AddressPort;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.AddressResponseDto;
import com.gasfgrv.logistics.order.infrastructure.integrations.rest.clients.ViaCepClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddressAdapter implements AddressPort {

    private final ViaCepClient client;

    @Override
    public boolean isAnExistingAddress(String zipCode) {
        log.info("Checking if the zip code {} belongs to a valid address", zipCode);
        var address = client.getAddress(zipCode);
        log.info(address.toString());
        return Boolean.FALSE.equals(address.error());
    }

}
