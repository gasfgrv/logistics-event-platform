package com.gasfgrv.logistics.order.infrastructure.mappers;

import java.util.UUID;

import org.instancio.Instancio;
import org.instancio.Select;

import com.gasfgrv.logistics.order.infrastructure.dtos.rest.CancelOrderDto;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.CreateOrderDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DtoType {

    CREATE(Instancio.of(CreateOrderDto.class)
            .set(Select.field(CreateOrderDto::customerId), UUID.randomUUID())
            .generate(Select.field(CreateOrderDto::origin), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
            .generate(Select.field(CreateOrderDto::destination), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
            .generate(Select.field(CreateOrderDto::weight), gen -> gen.floats().range(1f, 100f))
            .create()),
    CANCEL(Instancio.of(CancelOrderDto.class)
            .set(Select.field(CancelOrderDto::orderId), UUID.randomUUID())
            .generate(Select.field(CancelOrderDto::reason), gen -> gen.text().word().noun())
            .create());

    private final Record dto;

}
