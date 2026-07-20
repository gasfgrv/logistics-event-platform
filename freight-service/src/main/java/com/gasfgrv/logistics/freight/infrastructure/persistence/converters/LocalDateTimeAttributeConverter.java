package com.gasfgrv.logistics.freight.infrastructure.persistence.converters;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public AttributeValue transformFrom(LocalDateTime input) {
        return AttributeValue.fromS(FORMATTER.format(input));
    }

    @Override
    public LocalDateTime transformTo(AttributeValue input) {
        return LocalDateTime.parse(input.s(), FORMATTER);
    }

    @Override
    public EnhancedType<LocalDateTime> type() {
        return EnhancedType.of(LocalDateTime.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

}
