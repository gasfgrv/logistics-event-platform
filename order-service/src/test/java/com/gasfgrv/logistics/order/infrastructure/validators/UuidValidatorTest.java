package com.gasfgrv.logistics.order.infrastructure.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gasfgrv.logistics.order.infrastructure.validators.annotations.ValidUUID;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class UuidValidatorTest {

    private ConstraintValidator<ValidUUID, UUID> uuidValidator;

    @BeforeEach
    void setUpTest() {
        uuidValidator = new UuidValidator();
    }

    @Test
    @DisplayName("UuidValidator must validate a UUID")
    void uuidValidatorMustValidateAUuid() {
        // Arrange
        UUID uuid = UUID.fromString(Instancio.gen().text().uuid().get());
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // Act
        boolean valid = uuidValidator.isValid(uuid, context);

        // Assert
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("UuidValidator should report that a UUID is invalid")
    void uuidValidatorShouldReportThatAUuidIsInvalid() {
        // Arrange
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // Act
        boolean valid = uuidValidator.isValid(uuid, context);

        // Assert
        assertThat(valid).isFalse();
    }

}
