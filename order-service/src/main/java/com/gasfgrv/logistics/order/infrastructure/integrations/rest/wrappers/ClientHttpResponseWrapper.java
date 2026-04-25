package com.gasfgrv.logistics.order.infrastructure.integrations.rest.wrappers;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class ClientHttpResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse response;
    private final byte[] body;

    @Override
    @NullMarked
    public HttpStatusCode getStatusCode() throws IOException {
        return response.getStatusCode();
    }

    @Override
    @NullMarked
    public String getStatusText() throws IOException {
        return response.getStatusText();
    }

    @Override
    @NullMarked
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(body);
    }

    @Override
    @NullMarked
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

    @Override
    public void close() {
        response.close();
    }

}
