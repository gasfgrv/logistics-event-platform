package com.gasfgrv.logistics.freight.infrastructure.persistence.converters;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeAttributeConverterTest {

    private LocalDateTimeAttributeConverter converter;

    @BeforeEach
    void setUpTest() {
        converter = new LocalDateTimeAttributeConverter();
    }

    @Test
    void shouldTransformFromLocalDateTimeToAttributeValue() {
        var input = getInput();

        var result = converter.transformFrom(input);

        assertNotNull(result);
        assertEquals(input.toString(), result.s());
    }

    @Test
    void shouldTransformToLocalDateTimeFromAttributeValue() {
        var expected = getInput();
        var input = AttributeValue.fromS(expected.toString());

        var result = converter.transformTo(input);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnCorrectEnhancedType() {
        var expected = EnhancedType.of(LocalDateTime.class);

        var result = converter.type();

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnStringAttributeValueType() {
        var expected = AttributeValueType.S;

        var result = converter.attributeValueType();

        assertEquals(expected, result);
    }

    @Test
    void shouldThrowExceptionWhenTransformingNullLocalDateTime() {
        assertThrows(NullPointerException.class, () -> converter.transformFrom(null));
    }

    @Test
    void shouldThrowExceptionWhenTransformingFromInvalidAttributeValue() {
        var input = AttributeValue.fromS("invalid-date");

        assertThrows(Exception.class, () -> converter.transformTo(input));
    }

    private LocalDateTime getInput() {
        return Instancio.gen()
                .temporal()
                .localDateTime()
                .get();
    }

}
