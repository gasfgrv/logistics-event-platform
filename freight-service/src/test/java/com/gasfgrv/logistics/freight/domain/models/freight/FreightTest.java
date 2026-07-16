package com.gasfgrv.logistics.freight.domain.models.freight;

import com.gasfgrv.logistics.freight.domain.models.enuns.FreightStatus;
import com.gasfgrv.logistics.freight.domain.models.order.Order;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreightTest {

    private Freight freight;

    @BeforeEach
    void setUp() {
        this.freight = Instancio.of(Freight.class)
                .set(Select.field(Freight::getId), UUID.randomUUID())
                .generate(Select.field(Freight::getDistanceKm), gen -> gen.doubles().range(1.0, 100.0))
                .generate(Select.field(Freight::getPrice), gen -> gen.math().bigDecimal().range(BigDecimal.ONE, BigDecimal.valueOf(1000L)).scale(2))
                .set(Select.field(Freight::getStatus), FreightStatus.CALCULATED)
                .generate(Select.field(Freight::getCreatedAt), gen -> gen.temporal().instant().past())
                .set(Select.field(Freight::getOrder), Instancio.create(Order.class))
                .create();
    }

    @Test
    void shouldSetFreightAsFailed() {
        freight.setAsFailed();
        assertEquals(FreightStatus.FAILED, freight.getStatus());
    }

    @Test
    void shouldSetFreightAsCancelled() {
        freight.setAsCancelled();
        assertEquals(FreightStatus.CANCELLED, freight.getStatus());
    }

}
