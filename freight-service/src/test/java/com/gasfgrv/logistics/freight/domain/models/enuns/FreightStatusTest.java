package com.gasfgrv.logistics.freight.domain.models.enuns;

import com.gasfgrv.logistics.freight.domain.exceptions.InvalidStatusException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FreightStatusTest {

    @ParameterizedTest
    @CsvSource({
            "calculated,CALCULATED",
            "cancelled,CANCELLED",
            "failed,FAILED"
    })
    void shouldReturnEnumWhenReceivingCorrectValue(String input, String expectedName) {
        var result = FreightStatus.fromValue(input);
        assertEquals(expectedName, result.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "", "CREATED", " created "})
    void mustThrowExceptionWhenIncorrectValue(String input) {
        assertThrows(InvalidStatusException.class, () -> FreightStatus.fromValue(input));
    }

}
