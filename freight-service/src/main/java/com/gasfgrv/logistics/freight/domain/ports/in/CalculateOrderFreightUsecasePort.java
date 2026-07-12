package com.gasfgrv.logistics.freight.domain.ports.in;

import com.gasfgrv.logistics.freight.domain.models.order.Order;

public interface CalculateOrderFreightUsecasePort {

    void calculate(Order order);

}
