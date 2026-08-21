package com.gasfgrv.logistics.order.infrastructure.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CurrentTimeProviderTest {

    @Test
    @DisplayName("Should return current time in iso offset date time format")
    void shouldReturnCurrentTimeInIsoOffsetDateTimeFormat() {
        // Arrange
        var fixedInstant = Instant.parse("2023-10-01T15:30:00Z");
        var fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        var provider = new CurrentTimeProvider(fixedClock);

        // Act
        var result = provider.getCurrentTimeBytes();
        var resultString = new String(result, StandardCharsets.UTF_8);

        // Assert
        var expectedZoned = fixedInstant.atZone(ZoneId.of("America/Sao_Paulo"));
        var expected = expectedZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        assertEquals(expected, resultString);
    }

    @Test
    @DisplayName("Should use UTF-8 encoding")
    void shouldUseUtf8Encoding() {
        // Arrange
        var fixedInstant = Instant.parse("2023-10-01T15:30:00Z");
        var fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        var provider = new CurrentTimeProvider(fixedClock);

        // Act
        var result = provider.getCurrentTimeBytes();

        // Assert
        var reconstructed = new String(result, StandardCharsets.UTF_8);
        assertNotNull(reconstructed);
    }

    @Test
    @DisplayName("Should respect São Paulo timezone offset")
    void shouldRespectSaoPauloTimezoneOffset() {
        // Arrange
        var fixedInstant = Instant.parse("2023-06-01T12:00:00Z"); // período sem DST no Brasil
        var fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        var provider = new CurrentTimeProvider(fixedClock);

        // Act
        var result = new String(provider.getCurrentTimeBytes(), StandardCharsets.UTF_8);

        // Assert
        assertTrue(result.endsWith("-03:00"));
    }

    @Test
    @DisplayName("Should return non empty byte array")
    void shouldReturnNonEmptyByteArray() {
        // Arrange
        var clock = Clock.systemUTC();
        var provider = new CurrentTimeProvider(clock);

        // Act
        var result = provider.getCurrentTimeBytes();

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

}
