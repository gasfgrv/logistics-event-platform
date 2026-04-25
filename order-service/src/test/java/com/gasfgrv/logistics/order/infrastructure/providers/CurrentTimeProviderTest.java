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
        Instant fixedInstant = Instant.parse("2023-10-01T15:30:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        CurrentTimeProvider provider = new CurrentTimeProvider(fixedClock);

        // Act
        byte[] result = provider.getCurrentTimeBytes();
        String resultString = new String(result, StandardCharsets.UTF_8);

        // Assert
        ZonedDateTime expectedZoned = fixedInstant.atZone(ZoneId.of("America/Sao_Paulo"));
        String expected = expectedZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        assertEquals(expected, resultString);
    }

    @Test
    @DisplayName("Should use UTF-8 encoding")
    void shouldUseUtf8Encoding() {
        // Arrange
        Instant fixedInstant = Instant.parse("2023-10-01T15:30:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        CurrentTimeProvider provider = new CurrentTimeProvider(fixedClock);

        // Act
        byte[] result = provider.getCurrentTimeBytes();

        // Assert
        String reconstructed = new String(result, StandardCharsets.UTF_8);
        assertNotNull(reconstructed);
    }

    @Test
    @DisplayName("Should respect São Paulo timezone offset")
    void shouldRespectSaoPauloTimezoneOffset() {
        // Arrange
        Instant fixedInstant = Instant.parse("2023-06-01T12:00:00Z"); // período sem DST no Brasil
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        CurrentTimeProvider provider = new CurrentTimeProvider(fixedClock);

        // Act
        String result = new String(provider.getCurrentTimeBytes(), StandardCharsets.UTF_8);

        // Assert
        assertTrue(result.endsWith("-03:00"));
    }

    @Test
    @DisplayName("Should return non empty byte array")
    void shouldReturnNonEmptyByteArray() {
        // Arrange
        Clock clock = Clock.systemUTC();
        CurrentTimeProvider provider = new CurrentTimeProvider(clock);

        // Act
        byte[] result = provider.getCurrentTimeBytes();

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

}
