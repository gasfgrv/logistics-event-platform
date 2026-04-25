package com.gasfgrv.logistics.order.domain.exceptions;

public class OrderAlreadyBeenCancelled extends DomainException {

    public OrderAlreadyBeenCancelled() {
        super("This order has already been cancelled");
    }

}
