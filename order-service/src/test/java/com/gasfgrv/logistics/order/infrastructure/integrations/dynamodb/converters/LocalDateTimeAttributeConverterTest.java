package com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class LocalDateTimeAttributeConverterTest {

    private LocalDateTimeAttributeConverter converter;

    @BeforeEach
    void setUpTest() {
        converter = new LocalDateTimeAttributeConverter();
    }

    @Test
    @DisplayName("Should transform from local date time to attribute value")
    void shouldTransformFromLocalDateTimeToAttributeValue() {
        // Arrange
        LocalDateTime input = getInput();

        // Act
        AttributeValue result = converter.transformFrom(input);

        // Assert
        assertNotNull(result);
        assertEquals(input.toString(), result.s());
    }

    @Test
    @DisplayName("Should transform to local date time from attribute value")
    void shouldTransformToLocalDateTimeFromAttributeValue() {
        // Arrange
        LocalDateTime expected = getInput();
        AttributeValue input = AttributeValue.fromS(expected.toString());

        // Act
        LocalDateTime result = converter.transformTo(input);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should return correct enhanced type")
    void shouldReturnCorrectEnhancedType() {
        // Arrange
        EnhancedType<LocalDateTime> expected = EnhancedType.of(LocalDateTime.class);

        // Act
        EnhancedType<LocalDateTime> result = converter.type();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should return string attribute value type")
    void shouldReturnStringAttributeValueType() {
        // Arrange
        AttributeValueType expected = AttributeValueType.S;

        // Act
        AttributeValueType result = converter.attributeValueType();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should throw exception when transforming null local date time")
    void shouldThrowExceptionWhenTransformingNullLocalDateTime() {
        // Arrange
        LocalDateTime input = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> converter.transformFrom(input));
    }

    @Test
    @DisplayName("Should throw exception when transforming from invalid attribute value")
    void shouldThrowExceptionWhenTransformingFromInvalidAttributeValue() {
        // Arrange
        AttributeValue input = AttributeValue.fromS("invalid-date");

        // Act & Assert
        assertThrows(Exception.class, () -> converter.transformTo(input));
    }

    private LocalDateTime getInput() {
        return Instancio.gen()
                .temporal()
                .localDateTime()
                .get();
    }

}
