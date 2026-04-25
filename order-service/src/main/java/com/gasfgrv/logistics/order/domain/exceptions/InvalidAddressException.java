package com.gasfgrv.logistics.order.domain.exceptions;

public class InvalidAddressException extends DomainException {

    public InvalidAddressException() {
        super("Invalid address in origin or destination");
    }

}
