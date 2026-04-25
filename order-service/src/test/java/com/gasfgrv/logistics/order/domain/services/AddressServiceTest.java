package com.gasfgrv.logistics.order.domain.services;

import com.gasfgrv.logistics.order.domain.exceptions.InvalidAddressException;
import com.gasfgrv.logistics.order.domain.ports.out.AddressPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressPort addressPort;

    @InjectMocks
    private AddressService addressService;

    @Test
    @DisplayName("AddressService should throw an InvalidAddressException when it cannot find an existing address")
    void addressServiceShouldThrowAnInvalidAddressExceptionWhenItCannotFindAnExistingAddress() {
        // Arrange
        doReturn(false)
                .when(addressPort).isAnExistingAddress(anyString());

        // Act & Assert
        assertThatExceptionOfType(InvalidAddressException.class)
                .isThrownBy(() -> addressService.isValidAddress("12345-123"))
                .withMessageContaining("Invalid address in origin or destination");
    }

    @Test
    @DisplayName("AddressService should not throw any exception when it find an existing address")
    void addressServiceShouldNotAnyExceptionWhenItFindAnExistingAddress() {
        // Arrange
        doReturn(true)
                .when(addressPort).isAnExistingAddress(anyString());

        // Act & Assert
        assertThatCode(() -> addressService.isValidAddress("12345-123"))
                .doesNotThrowAnyException();
    }

}