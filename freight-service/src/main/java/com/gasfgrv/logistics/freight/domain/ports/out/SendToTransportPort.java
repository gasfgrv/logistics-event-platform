package com.gasfgrv.logistics.freight.domain.ports.out;

import com.gasfgrv.logistics.freight.domain.models.freight.Freight;

public interface SendToTransportPort {

    void sendFreightToTransport(Freight freight);

}
