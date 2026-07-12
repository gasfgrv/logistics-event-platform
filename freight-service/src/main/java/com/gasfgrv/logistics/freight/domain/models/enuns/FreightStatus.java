package com.gasfgrv.logistics.freight.domain.models.enuns;

import com.gasfgrv.logistics.freight.domain.exceptions.InvalidStatusException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum FreightStatus {

    CALCULATED("calculated"),
    FAILED("failed");

    @Getter
    private final String status;

    public static FreightStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(status -> value.equals(status.getStatus()))
                .findFirst()
                .orElseThrow(() -> new InvalidStatusException(value));
    }

}
