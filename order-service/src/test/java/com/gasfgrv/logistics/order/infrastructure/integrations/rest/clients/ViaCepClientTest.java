package com.gasfgrv.logistics.order.infrastructure.integrations.rest.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.logistics.order.infrastructure.dtos.rest.AddressResponseDto;
import com.gasfgrv.logistics.order.infrastructure.exceptions.ViaCepException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableWireMock
@TestPropertySource(properties = {
        "viacep.api.base-url=http://localhost:${wiremock.server.port}"
})
class ViaCepClientTest {

    @Autowired
    private ViaCepClient viaCepClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("ViaCepClient must find a valid address")
    void viacepclientMustFindAValidAddress() throws JsonProcessingException {
        // Arrange
        String zipCode = "01310100";
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

        stubFor(get(urlEqualTo("/" + zipCode + "/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedResponse))));

        // Act
        AddressResponseDto actualResponse = viaCepClient.getAddress(zipCode);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.zipCode()).isEqualTo(expectedResponse.zipCode());
        assertThat(actualResponse.publicPlace()).isEqualTo(expectedResponse.publicPlace());
        assertThat(actualResponse.error()).isFalse();
    }

    @Test
    @DisplayName("ViaCepClient deve lançar ViaCepException para CEP inválido")
    void viacepclientMustThrowExceptionForInvalidZipCode() throws Exception {
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

        stubFor(get(urlEqualTo("/" + zipCode + "/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(errorResponse))));

        // Act
        AddressResponseDto response = viaCepClient.getAddress(zipCode);

        // Assert
        assertThat(response.error()).isTrue();
    }

    @Test
    @DisplayName("ViaCepClient deve lançar ViaCepException para erro HTTP")
    void viacepclientMustThrowExceptionForHttpError() {
        // Arrange
        String zipCode = "01001000";

        stubFor(get(urlEqualTo("/" + zipCode + "/json"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Act & Assert
        assertThatExceptionOfType(ViaCepException.class)
                .isThrownBy(() -> viaCepClient.getAddress(zipCode))
                .withMessageContaining("Error calling the API");
    }

}
