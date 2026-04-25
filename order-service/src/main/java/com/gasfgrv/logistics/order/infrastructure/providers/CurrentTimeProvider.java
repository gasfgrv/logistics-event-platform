package com.gasfgrv.logistics.order.infrastructure.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CurrentTimeProvider {

    private final Clock clock;

    public byte[] getCurrentTimeBytes() {
        return Instant.now(clock)
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .getBytes(StandardCharsets.UTF_8);
    }

}
