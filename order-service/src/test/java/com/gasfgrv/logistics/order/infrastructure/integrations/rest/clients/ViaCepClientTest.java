package com.gasfgrv.logistics.order.infrastructure.integrations.rest.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.AddressResponseDto;
import com.gasfgrv.logistics.order.infrastructure.exceptions.ViaCepException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@EnableWireMock
@TestPropertySource(properties = {"viacep.api.base-url=http://localhost:${wiremock.server.port}"})
class ViaCepClientTest {

    @Autowired
    private ViaCepClient viaCepClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("ViaCepClient must find a valid address and verify request details")
    void viacepclientMustFindAValidAddress() throws JsonProcessingException {
        // Arrange
        String zipCode = "01310100";
        String testUrl = "/%s/json".formatted(zipCode);
        AddressResponseDto expectedResponse = new AddressResponseDto(
                "01310-100",
                "Avenida Paulista",
                "de 612 a 1510 - lado par",
                "",
                "Bela Vista",
                "São Paulo",
                "SP",
                "São Paulo",
                "Sudeste",
                "3550308",
                "1004",
                "11",
                "7107",
                false
        );

        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(expectedResponse))));

        // Act
        AddressResponseDto actualResponse = viaCepClient.getAddress(zipCode);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.zipCode()).isEqualTo(expectedResponse.zipCode());
        assertThat(actualResponse.error()).isFalse();

        verify(getRequestedFor(urlEqualTo(testUrl))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    @DisplayName("ViaCepClient must handle the 'error' flag from ViaCep API (200 OK with erro:true)")
    void viacepclientMustHandleApiErrorFlag() throws Exception {
        // Arrange
        String zipCode = "99999999";
        AddressResponseDto errorResponse = new AddressResponseDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true
        );

        stubFor(get(urlEqualTo("/%s/json".formatted(zipCode)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(errorResponse))));

        // Act
        AddressResponseDto response = viaCepClient.getAddress(zipCode);

        // Assert
        assertThat(response.error()).isTrue();
    }

    @Test
    @DisplayName("ViaCepClient must throw ViaCepException for server-side errors (5xx)")
    void viacepclientMustThrowExceptionForHttp500Error() {
        // Arrange
        String zipCode = "01001000";

        stubFor(get(urlEqualTo("/%s/json".formatted(zipCode)))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Act & Assert
        assertThatExceptionOfType(ViaCepException.class)
                .isThrownBy(() -> viaCepClient.getAddress(zipCode))
                .withMessageContaining("Error calling the API");
    }

    @Test
    @DisplayName("ViaCepClient must throw ViaCepException for client-side errors (4xx)")
    void viacepclientMustThrowExceptionForHttp400Error() {
        // Arrange
        String zipCode = "invalid";

        stubFor(get(urlEqualTo("/%s/json".formatted(zipCode)))
                .willReturn(aResponse()
                        .withStatus(400)));

        // Act & Assert
        assertThatExceptionOfType(ViaCepException.class)
                .isThrownBy(() -> viaCepClient.getAddress(zipCode))
                .withMessageContaining("Error calling the API");
    }

    @Test
    @DisplayName("ViaCepClient must handle malformed JSON responses")
    void viacepclientMustHandleMalformedJsonResponse() {
        // Arrange
        String zipCode = "01001000";

        stubFor(get(urlEqualTo("/%s/json".formatted(zipCode)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ invalid json }")));

        // Act & Assert
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> viaCepClient.getAddress(zipCode));
    }

    @Test
    @DisplayName("ViaCepClient must handle simulated network delay")
    void viacepclientMustHandleNetworkDelay() {
        // Arrange
        String zipCode = "01001000";

        stubFor(get(urlEqualTo("/%s/json".formatted(zipCode)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(1000) // 1 second delay
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}")));

        // Act
        AddressResponseDto response = viaCepClient.getAddress(zipCode);

        // Assert
        assertThat(response).isNotNull();
    }

}
