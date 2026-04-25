package com.gasfgrv.logistics.order.domain.ports.out;

public interface AddressPort {
    boolean isAnExistingAddress(String zipCode);
}
