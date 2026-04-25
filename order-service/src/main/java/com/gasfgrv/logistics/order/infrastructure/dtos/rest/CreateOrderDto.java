package com.gasfgrv.logistics.order.infrastructure.dtos.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gasfgrv.logistics.order.infrastructure.validators.annotations.ValidUUID;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateOrderDto(
        @JsonProperty("customer_id")
        @NotNull(message = "Customer ID is required")
        @ValidUUID
        UUID customerId,

        @NotBlank(message = "The origin ZIP code is required")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Invalid ZIP code format")
        String origin,

        @NotBlank(message = "The destination ZIP code is required")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Invalid ZIP code format")
        String destination,

        @NotNull(message = "Weight is mandatory")
        @Positive(message = "The weight must be greater than zero")
        @DecimalMax(value = "100.00", message = "The maximum permitted weight is 100 kg.")
        Float weight
) {
}
