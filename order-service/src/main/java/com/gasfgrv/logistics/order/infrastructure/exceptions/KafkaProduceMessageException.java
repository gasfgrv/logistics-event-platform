package com.gasfgrv.logistics.order.infrastructure.exceptions;

public class KafkaProduceMessageException extends InfrastructureException {

    public KafkaProduceMessageException(String message) {
        super(message);
    }

}
