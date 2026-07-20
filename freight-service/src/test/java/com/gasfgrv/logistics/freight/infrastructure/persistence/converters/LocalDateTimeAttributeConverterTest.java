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
        LocalDateTime input = getInput();

        AttributeValue result = converter.transformFrom(input);

        assertNotNull(result);
        assertEquals(input.toString(), result.s());
    }

    @Test
    void shouldTransformToLocalDateTimeFromAttributeValue() {
        LocalDateTime expected = getInput();
        AttributeValue input = AttributeValue.fromS(expected.toString());

        LocalDateTime result = converter.transformTo(input);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnCorrectEnhancedType() {
        EnhancedType<LocalDateTime> expected = EnhancedType.of(LocalDateTime.class);

        EnhancedType<LocalDateTime> result = converter.type();

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnStringAttributeValueType() {
        AttributeValueType expected = AttributeValueType.S;

        AttributeValueType result = converter.attributeValueType();

        assertEquals(expected, result);
    }

    @Test
    void shouldThrowExceptionWhenTransformingNullLocalDateTime() {
        LocalDateTime input = null;

        assertThrows(NullPointerException.class, () -> converter.transformFrom(input));
    }

    @Test
    void shouldThrowExceptionWhenTransformingFromInvalidAttributeValue() {
        AttributeValue input = AttributeValue.fromS("invalid-date");

        assertThrows(Exception.class, () -> converter.transformTo(input));
    }

    private LocalDateTime getInput() {
        return Instancio.gen()
                .temporal()
                .localDateTime()
                .get();
    }

}
