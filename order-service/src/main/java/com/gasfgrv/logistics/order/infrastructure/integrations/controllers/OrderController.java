package com.gasfgrv.logistics.order.infrastructure.integrations.controllers;

import com.gasfgrv.logistics.order.domain.commands.CancelOrderCommand;
import com.gasfgrv.logistics.order.domain.commands.CreateOrderCommand;
import com.gasfgrv.logistics.order.domain.ports.in.UsecasePort;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.CancelOrderDto;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.CreateOrderDto;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.OrderResponseDto;
import com.gasfgrv.logistics.order.infrastructure.mappers.OrderMapper;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/{version}/orders", version = "v1")
public class OrderController {

    private final UsecasePort<CreateOrderCommand> createOrderUsecase;
    private final UsecasePort<CancelOrderCommand> cancelOrderUsecase;
    private final OrderMapper mapper;

    @PostMapping("/create")
    @ApiResponse(responseCode = "202")
    public ResponseEntity<OrderResponseDto> createNewOrder(@Valid @RequestBody CreateOrderDto input, HttpServletRequest request) {
        log.info("[{}] - {}: Creating new order", request.getMethod(), request.getRequestURI());
        createOrderUsecase.execute(mapper.toCreateCommand(input));
        return ResponseEntity.accepted()
                .body(mapper.toResponse(input));
    }

    @PutMapping("/cancel")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> cancelOrder(@Valid @RequestBody CancelOrderDto input, HttpServletRequest request) {
        log.info("[{}] - {}: Cancelling order", request.getMethod(), request.getRequestURI());
        cancelOrderUsecase.execute(mapper.toCancelCommand(input));
        return ResponseEntity.noContent()
                .build();
    }

}
