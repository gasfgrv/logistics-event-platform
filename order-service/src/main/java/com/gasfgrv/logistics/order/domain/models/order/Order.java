package com.gasfgrv.logistics.order.domain.models.order;

import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    public String getIdValue() {
        return id.toString();
    }
}
