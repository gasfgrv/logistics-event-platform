package com.gasfgrv.logistics.order.infrastructure.integrations.controllers;

import com.gasfgrv.logistics.order.domain.exceptions.InvalidAddressException;
import com.gasfgrv.logistics.order.domain.exceptions.InvalidStatusException;
import com.gasfgrv.logistics.order.domain.exceptions.NonExistingOrderException;
import com.gasfgrv.logistics.order.domain.exceptions.OrderAlreadyBeenCancelled;
import com.gasfgrv.logistics.order.infrastructure.dtos.handler.ApiError;
import com.gasfgrv.logistics.order.infrastructure.exceptions.KafkaProduceMessageException;
import com.gasfgrv.logistics.order.infrastructure.exceptions.ViaCepException;
import com.gasfgrv.logistics.order.infrastructure.providers.CurrentTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class OrderControllerAdvice {

    private final CurrentTimeProvider currentTimeProvider;

    @ExceptionHandler(InvalidAddressException.class)
    public ResponseEntity<ApiError> handleException(InvalidAddressException exception) {
        var status = HttpStatus.BAD_REQUEST;
        return buildResponse(status, buildErrorMessage(status, Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ApiError> handleException(InvalidStatusException exception) {
        var status = HttpStatus.UNPROCESSABLE_CONTENT;
        return buildResponse(status, buildErrorMessage(status, Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(NonExistingOrderException.class)
    public ResponseEntity<ApiError> handleException(NonExistingOrderException exception) {
        var status = HttpStatus.NOT_FOUND;
        return buildResponse(status, buildErrorMessage(status, Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(OrderAlreadyBeenCancelled.class)
    public ResponseEntity<ApiError> handleException(OrderAlreadyBeenCancelled exception) {
        var status = HttpStatus.CONFLICT;
        return buildResponse(status, buildErrorMessage(status, Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(KafkaProduceMessageException.class)
    public ResponseEntity<ApiError> handleException(KafkaProduceMessageException exception) {
        var status = HttpStatus.BAD_GATEWAY;
        return buildResponse(status, buildErrorMessage(status, Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(ViaCepException.class)
    public ResponseEntity<ApiError> handleException(ViaCepException exception) {
        var status = HttpStatus.BAD_GATEWAY;
        return buildResponse(status, buildErrorMessage(status, Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception exception) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildResponse(status, buildErrorMessage(status, Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleException(MethodArgumentNotValidException exception) {
        var status = HttpStatus.BAD_REQUEST;
        var errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> "%s: %s with value '%s'".formatted(fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue()))
                .toList();
        return buildResponse(status, buildErrorMessage(status, errors));
    }

    private ApiError buildErrorMessage(HttpStatus status, List<String> errors) {
        var currentTime = new String(currentTimeProvider.getCurrentTimeBytes(), StandardCharsets.UTF_8);
        return ApiError.builder()
                .timestamp(OffsetDateTime.parse(currentTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .code(status.value())
                .status(status.name())
                .errors(errors)
                .build();
    }

    private static ResponseEntity<ApiError> buildResponse(HttpStatus status, ApiError error) {
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(error);
    }

}
