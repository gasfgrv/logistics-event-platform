package com.gasfgrv.logistics.order.infrastructure.integrations.rest.interceptors;

import com.gasfgrv.logistics.order.infrastructure.integrations.rest.wrappers.ClientHttpResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    @NullMarked
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        log.info("HTTP Request: {} {}", request.getMethod(), request.getURI());
        var response = execution.execute(request, body);
        log.info("HTTP Response: {}", response.getStatusCode());
        return new ClientHttpResponseWrapper(response, response.getBody().readAllBytes());
    }

}
