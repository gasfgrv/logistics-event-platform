package com.gasfgrv.logistics.order.infrastructure.validators;

import com.gasfgrv.logistics.order.infrastructure.validators.annotations.ValidUUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class UuidValidator implements ConstraintValidator<ValidUUID, UUID> {

    private static final String NIL_UUID = "00000000-0000-0000-0000-000000000000";

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        return !NIL_UUID.equals(value.toString());
    }

}
