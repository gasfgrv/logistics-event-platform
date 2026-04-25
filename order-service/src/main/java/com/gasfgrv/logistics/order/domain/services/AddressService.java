package com.gasfgrv.logistics.order.domain.services;

import com.gasfgrv.logistics.order.domain.exceptions.InvalidAddressException;
import com.gasfgrv.logistics.order.domain.ports.out.AddressPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddressService {

    private final AddressPort addresses;

    public void isValidAddress(String zipCode) {
        if (!addresses.isAnExistingAddress(zipCode)) {
            throw new InvalidAddressException();
        }
    }

}
