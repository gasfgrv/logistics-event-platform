package com.gasfgrv.logistics.order.infrastructure.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "logistics order service",
                description = "Order creation and cancel",
                version = "v1",
                contact = @Contact(
                        name = "gasfgrv",
                        email = "gustavo_almeida11@hotmail.com",
                        url = "https://github.com/gasfgrv"
                )
        )
)
public class OpenApiConfiguration {

    @Bean
    public GroupedOpenApi openApi() {
        return GroupedOpenApi.builder()
                .group("order-controllers")
                .packagesToScan("com.gasfgrv.logistics.order.infrastructure.integrations.controllers")
                .pathsToMatch("/**")
                .build();
    }

}
