package com.gasfgrv.logistics.freight.domain.ports.out;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;

public interface NotifyCancellationPort {

    void notifyCancellation(Freight freight);

}
