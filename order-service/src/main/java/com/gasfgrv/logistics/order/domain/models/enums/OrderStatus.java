package com.gasfgrv.logistics.order.domain.models.enums;

import com.gasfgrv.logistics.order.domain.exceptions.InvalidStatusException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum OrderStatus {

    CREATED("created"),
    CANCELLED("canceled"),
    FAILED("failed");

    @Getter
    private final String status;

    public static OrderStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(status -> value.equals(status.getStatus()))
                .findFirst()
                .orElseThrow(() -> new InvalidStatusException(value));
    }
}
