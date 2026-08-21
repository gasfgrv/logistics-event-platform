package com.gasfgrv.logistics.order.infrastructure.integrations.rest.wrappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

class ClientHttpResponseWrapperTest {

    @Test
    @DisplayName("Should return status code from wrapped response")
    void shouldReturnStatusCodeFromWrappedResponse() throws IOException {
        // Arrange
        var mockResponse = mock(ClientHttpResponse.class);
        var expectedStatus = HttpStatus.OK;

        when(mockResponse.getStatusCode()).thenReturn(expectedStatus);

        var wrapper = new ClientHttpResponseWrapper(mockResponse, new byte[] {});

        // Act
        var actualStatus = wrapper.getStatusCode();

        // Assert
        assertThat(actualStatus).isEqualTo(expectedStatus);
        verify(mockResponse).getStatusCode();

        wrapper.close();
    }

    @Test
    @DisplayName("Should return status text from wrapped response")
    void shouldReturnStatusTextFromWrappedResponse() throws IOException {
        // Arrange
        var mockResponse = mock(ClientHttpResponse.class);
        var expectedText = "OK";

        when(mockResponse.getStatusText()).thenReturn(expectedText);

        var wrapper = new ClientHttpResponseWrapper(mockResponse, new byte[] {});

        // Act
        var actualText = wrapper.getStatusText();

        // Assert
        assertThat(actualText).isEqualTo(expectedText);
        verify(mockResponse).getStatusText();

        wrapper.close();
    }

    @Test
    @DisplayName("Should return body as new input stream with provided bytes")
    void shouldReturnBodyAsNewInputStreamWithProvidedBytes() throws IOException {
        // Arrange
        var mockResponse = mock(ClientHttpResponse.class);
        var body = "test-body".getBytes();

        var wrapper = new ClientHttpResponseWrapper(mockResponse, body);

        // Act
        var inputStream1 = wrapper.getBody();
        var inputStream2 = wrapper.getBody();

        var read1 = inputStream1.readAllBytes();
        var read2 = inputStream2.readAllBytes();

        // Assert
        assertThat(read1).isEqualTo(body);
        assertThat(read2).isEqualTo(body);
        assertThat(inputStream1).isNotSameAs(inputStream2);

        wrapper.close();
    }

    @Test
    @DisplayName("Should return headers from wrapped response")
    void shouldReturnHeadersFromWrappedResponse() {
        // Arrange
        var mockResponse = mock(ClientHttpResponse.class);
        var expectedHeaders = new HttpHeaders();

        when(mockResponse.getHeaders()).thenReturn(expectedHeaders);

        var wrapper = new ClientHttpResponseWrapper(mockResponse, new byte[] {});

        // Act
        var actualHeaders = wrapper.getHeaders();

        // Assert
        assertThat(actualHeaders).isEqualTo(expectedHeaders);
        verify(mockResponse).getHeaders();

        wrapper.close();
    }

    @Test
    @DisplayName("Should delegate close to wrapped response")
    void shouldDelegateCloseToWrappedResponse() {
        // Arrange
        var mockResponse = mock(ClientHttpResponse.class);
        var wrapper = new ClientHttpResponseWrapper(mockResponse, new byte[] {});

        // Act
        wrapper.close();

        // Assert
        verify(mockResponse).close();
    }

}
