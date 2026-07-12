package com.gasfgrv.logistics.freight.domain.ports.out;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;

public interface NotifyFailurePort {

    void notifyFailure(Freight freight, String errorCode, String errorMessage);

}
