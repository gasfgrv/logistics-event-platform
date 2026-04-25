package com.gasfgrv.logistics.order.domain.exceptions;

public class NonExistingOrderException extends DomainException {

    public NonExistingOrderException() {
        super("Non-existing order");
    }

}
