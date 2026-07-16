package com.gasfgrv.logistics.freight.domain.models.enuns;

import com.gasfgrv.logistics.freight.domain.exceptions.InvalidStatusException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStatusTest {

    @ParameterizedTest
    @CsvSource({
            "created,CREATED",
            "canceled,CANCELLED",
            "failed,FAILED"
    })
    void shouldReturnEnumWhenReceivingCorrectValue(String input, String expectedName) {
        OrderStatus result = OrderStatus.fromValue(input);
        assertEquals(expectedName, result.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "", "CREATED", " created "})
    void mustThrowExceptionWhenIncorrectValue(String input) {
        assertThrows(InvalidStatusException.class, () -> OrderStatus.fromValue(input));
    }
}
