package com.gasfgrv.logistics.order.infrastructure.integrations.rest.clients;

import com.gasfgrv.logistics.order.infrastructure.dtos.rest.AddressResponseDto;
import com.gasfgrv.logistics.order.infrastructure.exceptions.ViaCepException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViaCepClient {

    private final RestClient client;

    public AddressResponseDto getAddress(String zipCode) {
        log.info("Calling the ViaCep API to obtain the address");
        return client.get()
                .uri("/{zipCode}/json", zipCode)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ViaCepClient::executeErrorHandler)
                .body(AddressResponseDto.class);
    }

    private static void executeErrorHandler(HttpRequest request, ClientHttpResponse response) throws IOException {
        var statusText = response.getStatusText();
        log.error("Error when calling api: {}", statusText);
        throw new ViaCepException(statusText);
    }

}
