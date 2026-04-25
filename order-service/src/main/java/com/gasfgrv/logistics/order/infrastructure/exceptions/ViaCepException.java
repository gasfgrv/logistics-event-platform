package com.gasfgrv.logistics.order.infrastructure.exceptions;

public class ViaCepException extends InfrastructureException {

    public ViaCepException(String statusText) {
        super("Error calling the API: %s".formatted(statusText));
    }

}
