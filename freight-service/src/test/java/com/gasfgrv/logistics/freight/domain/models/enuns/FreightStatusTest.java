package com.gasfgrv.logistics.freight.domain.models.enuns;

import com.gasfgrv.logistics.freight.domain.exceptions.InvalidStatusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FreightStatusTest {

    @ParameterizedTest
    @CsvSource({
            "calculated,CALCULATED",
            "cancelled,CANCELLED",
            "failed,FAILED"
    })
    void shouldReturnEnumWhenReceivingCorrectValue(String input, String expectedName) {
        FreightStatus result = FreightStatus.fromValue(input);
        Assertions.assertEquals(expectedName, result.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "", "CREATED", " created "})
    void mustThrowExceptionWhenIncorrectValue(String input) {
        assertThrows(InvalidStatusException.class, () -> FreightStatus.fromValue(input));
    }

}