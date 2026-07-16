package com.gasfgrv.logistics.freight.domain.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateFreightException extends DomainException {

    private String errorCode;
    private String errorMessage;

    public CalculateFreightException(String message, Throwable cause) {
        super(message, cause);
    }

}
