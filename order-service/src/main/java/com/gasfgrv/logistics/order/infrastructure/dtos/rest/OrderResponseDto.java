package com.gasfgrv.logistics.order.infrastructure.dtos.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record OrderResponseDto(
        @JsonProperty("customer_id")
        UUID customerId,
        String origin,
        String destination,
        float weight
) {
}
