package com.gasfgrv.logistics.freight.domain.models.freight;

import com.gasfgrv.logistics.freight.domain.models.enuns.FreightStatus;
import com.gasfgrv.logistics.freight.domain.models.mocks.EntityMockFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreightTest {

    private Freight freight;

    @BeforeEach
    void setUp() {
        this.freight = EntityMockFactory.buildFreight(null);
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
