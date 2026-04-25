package com.gasfgrv.logistics.order.infrastructure.dtos.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gasfgrv.logistics.order.infrastructure.validators.annotations.ValidUUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CancelOrderDto(
        @JsonProperty("order_id")
        @NotNull(message = "Order ID is required")
        @ValidUUID
        UUID orderId,

        @NotBlank(message = "The reason for cancellation is mandatory")
        @Size(min = 10, max = 120, message = "The reason for cancellation must be between 10 and 120 characters")
        String reason
) {
}
