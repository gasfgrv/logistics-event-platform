package com.gasfgrv.logistics.order.infrastructure.dtos.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record ApiError(
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss") OffsetDateTime timestamp,
        Integer code,
        String status,
        List<String> errors
) {
}
