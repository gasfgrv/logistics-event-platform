package com.gasfgrv.logistics.freight;

import org.springframework.boot.SpringApplication;

public class TestFreightServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(FreightServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
