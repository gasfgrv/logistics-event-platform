package com.gasfgrv.logistics.order.infrastructure.configurations;

import com.gasfgrv.logistics.order.infrastructure.configurations.properties.ViacepApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {

    private final ClientHttpRequestInterceptor loggingInterceptor;
    private final ViacepApiProperties viacepApiProperties;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
                .baseUrl(viacepApiProperties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(loggingInterceptor)
                .build();
    }

}
