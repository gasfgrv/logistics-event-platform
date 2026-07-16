package com.gasfgrv.logistics.freight.domain.ports.out;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.domain.models.order.Order;

public interface NotifyFailurePort {

    void notifyFailure(Order order, String errorCode, String errorMessage);

}
