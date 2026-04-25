package com.gasfgrv.logistics.order.infrastructure.configurations;

import com.gasfgrv.logistics.order.domain.services.AddressService;
import com.gasfgrv.logistics.order.domain.services.OrderService;
import com.gasfgrv.logistics.order.infrastructure.adapters.AddressAdapter;
import com.gasfgrv.logistics.order.infrastructure.adapters.OrderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ServiceBeansConfiguration {

    @Bean
    public AddressService addressService(AddressAdapter addressAdapter) {
        return new AddressService(addressAdapter);
    }

    @Bean
    public OrderService orderService(Clock clock, OrderAdapter orderAdapter) {
        return new OrderService(clock, orderAdapter);
    }

}
