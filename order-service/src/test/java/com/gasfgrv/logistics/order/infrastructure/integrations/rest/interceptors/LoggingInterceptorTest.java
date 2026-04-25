package com.gasfgrv.logistics.order.infrastructure.integrations.rest.interceptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import com.gasfgrv.logistics.order.infrastructure.integrations.rest.wrappers.ClientHttpResponseWrapper;

class LoggingInterceptorTest {

    @Test
    @DisplayName("Should intercept request and wrap response")
    void shouldInterceptRequestAndWrapResponse() throws IOException {
        // Arrange
        HttpRequest request = mock(HttpRequest.class);
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        byte[] requestBody = "request".getBytes();
        byte[] responseBody = "response".getBytes();

        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost/test"));

        when(execution.execute(request, requestBody)).thenReturn(response);
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(responseBody));

        LoggingInterceptor interceptor = new LoggingInterceptor();

        // Act
        ClientHttpResponse result = interceptor.intercept(request, requestBody, execution);

        // Assert
        assertThat(result).isInstanceOf(ClientHttpResponseWrapper.class);
        verify(execution).execute(request, requestBody);
        verify(response).getStatusCode();
        verify(response).getBody();
    }

    @Test
    @DisplayName("Should preserve response body content in wrapper")
    void shouldPreserveResponseBodyContentInWrapper() throws IOException {
        // Arrange
        HttpRequest request = mock(HttpRequest.class);
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        byte[] responseBody = "response-body".getBytes();

        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getURI()).thenReturn(URI.create("http://localhost/test"));

        when(execution.execute(any(), any())).thenReturn(response);
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(responseBody));

        LoggingInterceptor interceptor = new LoggingInterceptor();

        // Act
        ClientHttpResponse wrapped = interceptor.intercept(request, new byte[] {}, execution);
        byte[] actualBody = wrapped.getBody().readAllBytes();

        // Assert
        assertThat(actualBody).isEqualTo(responseBody);
    }

    @Test
    @DisplayName("Should call execution with same request and body")
    void shouldCallExecutionWithSameRequestAndBody() throws IOException {
        // Arrange
        HttpRequest request = mock(HttpRequest.class);
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        byte[] requestBody = "payload".getBytes();

        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        when(request.getURI()).thenReturn(URI.create("http://localhost/test"));

        when(execution.execute(request, requestBody)).thenReturn(response);
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(new byte[] {}));

        LoggingInterceptor interceptor = new LoggingInterceptor();

        // Act
        interceptor.intercept(request, requestBody, execution);

        // Assert
        verify(execution).execute(request, requestBody);
    }

    @Test
    @DisplayName("Should read response body only once from original response")
    void shouldReadResponseBodyOnlyOnceFromOriginalResponse() throws IOException {
        // Arrange
        HttpRequest request = mock(HttpRequest.class);
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        byte[] responseBody = "response".getBytes();
        ByteArrayInputStream inputStream = spy(new ByteArrayInputStream(responseBody));

        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost/test"));

        when(execution.execute(any(), any())).thenReturn(response);
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getBody()).thenReturn(inputStream);

        LoggingInterceptor interceptor = new LoggingInterceptor();

        // Act
        interceptor.intercept(request, new byte[] {}, execution);

        // Assert
        verify(response, times(1)).getBody();
    }

}
