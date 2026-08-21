package com.gasfgrv.logistics.freight.application;

import com.gasfgrv.logistics.freight.application.exceptions.ApplicationException;
import com.gasfgrv.logistics.freight.domain.exceptions.CalculateFreightException;
import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.domain.models.mocks.EntityMockFactory;
import com.gasfgrv.logistics.freight.domain.models.order.Order;
import com.gasfgrv.logistics.freight.domain.ports.out.CalculateFreightPort;
import com.gasfgrv.logistics.freight.domain.ports.out.FreightRepositoryPort;
import com.gasfgrv.logistics.freight.domain.ports.out.NotifyFailurePort;
import com.gasfgrv.logistics.freight.domain.ports.out.SendToTransportPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class CalculateOrderFreightUsecaseTest {

    @Mock
    private CalculateFreightPort calculateFreightClient;

    @Mock
    private FreightRepositoryPort repository;

    @Mock
    private SendToTransportPort transportNotifier;

    @Mock
    private NotifyFailurePort notifyFailurePort;

    @InjectMocks
    private CalculateOrderFreightUsecase usecase;

    private Order order;
    private Freight freight;

    @BeforeEach
    void setUp() {
        this.order = EntityMockFactory.buildOrder();
        this.freight = EntityMockFactory.buildFreight(this.order);
    }

    @Test
    void shouldCalculateFreightWhenReceiveOrder(CapturedOutput output) {
        doReturn(this.freight).when(this.calculateFreightClient).calculateFreight(this.order);
        doReturn(this.freight).when(this.repository).saveFreight(this.freight);

        usecase.calculate(this.order);

        assertTrue(output.getOut().contains("Calculating order freight"));
        assertTrue(output.getOut().contains("Sending order freight to transport"));

        verify(this.transportNotifier).sendFreightToTransport(this.freight);
        verify(this.notifyFailurePort, never()).notifyFailure(eq(this.order), anyString(), anyString());
    }

    @Test
    void shouldNotifyFailureWhenHaveFailureToCalculateFreight(CapturedOutput output) {
        var exception = new CalculateFreightException("NOT_FOUND", new RuntimeException("Mock Exception"));
        doThrow(exception).when(this.calculateFreightClient).calculateFreight(this.order);

        usecase.calculate(this.order);

        assertTrue(output.getOut().contains("Calculating order freight"));
        assertTrue(output.getOut().contains("Error while sending order freight to transport"));

        verify(this.repository, never()).saveFreight(any(Freight.class));
        verify(this.transportNotifier, never()).sendFreightToTransport(any(Freight.class));
        verify(this.notifyFailurePort).notifyFailure(this.order, exception.getErrorCode(), exception.getErrorMessage());
    }

    @Test
    void shouldThrowsApplicationException(CapturedOutput output) {
        doReturn(this.freight).when(this.calculateFreightClient).calculateFreight(this.order);
        doThrow(RuntimeException.class).when(this.repository).saveFreight(this.freight);

        var exception = assertThrows(ApplicationException.class, () -> usecase.calculate(this.order));
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertTrue(exception.getMessage().contains("Error while attempting to calculate freight"));

        assertTrue(output.getOut().contains("Calculating order freight"));
        assertTrue(output.getOut().contains("Error while sending order freight to transport"));

        verify(this.transportNotifier, never()).sendFreightToTransport(any(Freight.class));
        verify(this.notifyFailurePort, never()).notifyFailure(eq(this.order), anyString(), anyString());
    }

}
