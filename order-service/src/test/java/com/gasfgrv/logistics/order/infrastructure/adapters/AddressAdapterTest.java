package com.gasfgrv.logistics.order.infrastructure.adapters;

import com.gasfgrv.logistics.order.infrastructure.dtos.rest.AddressResponseDto;
import com.gasfgrv.logistics.order.infrastructure.integrations.rest.clients.ViaCepClient;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressAdapterTest {

    @Mock
    private ViaCepClient client;

    @InjectMocks
    private AddressAdapter adapter;

    @Test
    @DisplayName("AddressAdapter must validate an existing address")
    void addressadapterMustValidateAnExistingAddress() {
        // Arrange
        var zipCode = generateZipCode();
        var address = generateClientResponse(zipCode, false);
        doReturn(address).when(client).getAddress(zipCode);

        // Act
        var existingAddress = adapter.isAnExistingAddress(zipCode);

        // Assert
        assertThat(existingAddress).isTrue();
        verify(client).getAddress(zipCode);
    }

    @Test
    @DisplayName("AddressAdapter must validate a non existing address")
    void addressadapterMustValidateANonExistingAddress() {
        // Arrange
        var zipCode = generateZipCode();
        var address = generateClientResponse(zipCode, true);
        doReturn(address).when(client).getAddress(zipCode);

        // Act
        var existingAddress = adapter.isAnExistingAddress(zipCode);

        // Assert
        assertThat(existingAddress).isFalse();
        verify(client).getAddress(zipCode);
    }

    private static AddressResponseDto generateClientResponse(String zipCode, boolean isError) {
        return Instancio.of(AddressResponseDto.class)
                .set(Select.field(AddressResponseDto::zipCode), zipCode)
                .set(Select.field(AddressResponseDto::error), isError)
                .create();
    }

    private static String generateZipCode() {
        return Instancio.gen()
                .text()
                .pattern("#d#d#d#d#d-#d#d#d")
                .get();
    }

}