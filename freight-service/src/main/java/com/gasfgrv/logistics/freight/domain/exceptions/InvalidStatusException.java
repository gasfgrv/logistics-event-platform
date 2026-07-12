package com.gasfgrv.logistics.freight.domain.exceptions;

public class InvalidStatusException extends DomainException {

    public InvalidStatusException(String value) {
        super("Invalid order status: %s".formatted(value));
    }

}
