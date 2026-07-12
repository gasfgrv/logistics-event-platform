package com.gasfgrv.logistics.freight.domain.ports.out;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.domain.models.order.Order;

public interface CalculateFreightPort {

    Freight calculateFreight(Order order);

}
