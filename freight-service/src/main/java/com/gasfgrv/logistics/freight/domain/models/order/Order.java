package com.gasfgrv.logistics.freight.domain.models.order;

import com.gasfgrv.logistics.freight.domain.models.enuns.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Order {

    private UUID id;
    private UUID customerId;
    private String origin;
    private String destination;
    private float weight;
    private OrderStatus status;
    private LocalDateTime createdAt;

}
