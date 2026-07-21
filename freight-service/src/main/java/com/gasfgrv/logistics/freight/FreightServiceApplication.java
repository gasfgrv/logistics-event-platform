package com.gasfgrv.logistics.freight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FreightServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreightServiceApplication.class, args);
    }

}
