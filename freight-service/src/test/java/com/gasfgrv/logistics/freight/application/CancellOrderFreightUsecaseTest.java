package com.gasfgrv.logistics.freight.application;

import com.gasfgrv.logistics.freight.domain.exceptions.NoFreightACalculatedException;
import com.gasfgrv.logistics.freight.domain.models.enuns.FreightStatus;
import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.domain.models.mocks.EntityMockFactory;
import com.gasfgrv.logistics.freight.domain.models.order.Order;
import com.gasfgrv.logistics.freight.domain.ports.out.FreightRepositoryPort;
import com.gasfgrv.logistics.freight.domain.ports.out.NotifyCancellationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class CancellOrderFreightUsecaseTest {

    @Captor
    private ArgumentCaptor<Freight> freightCaptor;

    @Mock
    private FreightRepositoryPort repository;

    @Mock
    private NotifyCancellationPort notifier;

    @InjectMocks
    private CancellOrderFreightUsecase usecase;

    private Order order;

    private Freight freight;

    @BeforeEach
    void setUp() {
        this.order = EntityMockFactory.buildOrder();
        this.freight = EntityMockFactory.buildFreight(this.order);
    }

    @Test
    void shouldThrowExceptionWhenNoFreightCalculated(CapturedOutput output) {
        doReturn(false)
                .when(repository).freightHasAlreadyBeenCalculatedForTheOrder(this.order.getId());

        assertThrows(NoFreightACalculatedException.class, () -> this.usecase.cancelOrderFreight(this.order));

        assertTrue(output.getOut().contains("Cannot cancel order freight: no freight has been calculated for order"));
        assertFalse(output.getOut().contains("Canceling order freight for order"));

        verify(repository, never()).getFreightByOrder(this.order.getId());
        verify(notifier, never()).notifyCancellation(any(Freight.class));
    }

    @Test
    void shouldSuccessfullyCancelFreight(CapturedOutput output) {
        doReturn(true)
                .when(repository).freightHasAlreadyBeenCalculatedForTheOrder(this.order.getId());

        doReturn(Optional.of(this.freight))
                .when(repository).getFreightByOrder(this.order.getId());

        doNothing()
                .when(notifier).notifyCancellation(freightCaptor.capture());

        this.usecase.cancelOrderFreight(this.order);

        assertFalse(output.getOut().contains("Cannot cancel order freight: no freight has been calculated for order"));
        assertTrue(output.getOut().contains("Canceling order freight for order"));

        Freight value = freightCaptor.getValue();
        assertSame(this.freight, value);
        assertEquals(FreightStatus.CANCELLED, value.getStatus());

        verify(repository).freightHasAlreadyBeenCalculatedForTheOrder(this.order.getId());
        verify(repository).getFreightByOrder(this.order.getId());
        verify(notifier).notifyCancellation(value);
    }

}