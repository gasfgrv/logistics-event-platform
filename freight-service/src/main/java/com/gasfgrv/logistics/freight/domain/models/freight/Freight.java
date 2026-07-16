package com.gasfgrv.logistics.freight.domain.models.freight;

import com.gasfgrv.logistics.freight.domain.models.enuns.FreightStatus;
import com.gasfgrv.logistics.freight.domain.models.order.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Freight {

    private UUID id;
    private Order order;
    private double distanceKm;
    private BigDecimal price;
    private FreightStatus status;
    private Instant createdAt;

    public void setAsFailed() {
        this.status = FreightStatus.FAILED;
    }

    public void setAsCancelled() {
        this.status = FreightStatus.CANCELLED;
    }

}
