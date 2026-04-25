package com.gasfgrv.logistics.order.domain.exceptions;

public class InvalidStatusException extends DomainException {

    public InvalidStatusException(String value) {
        super("Invalid order status: " + value);
    }

}
