package com.gasfgrv.logistics.order.infrastructure.dtos.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddressResponseDto(
        @JsonProperty("cep") String zipCode,
        @JsonProperty("logradouro") String publicPlace,
        @JsonProperty("complemento") String complement,
        @JsonProperty("unidade") String unit,
        @JsonProperty("bairro") String district,
        @JsonProperty("localidade") String locale,
        @JsonProperty("uf") String federativeUnit,
        @JsonProperty("estado") String state,
        @JsonProperty("regiao") String region,
        @JsonProperty("ibge") String ibge,
        @JsonProperty("gia") String gia,
        @JsonProperty("ddd") String ddd,
        @JsonProperty("siafi") String siafi,
        @JsonProperty("erro") Boolean error
) {
    public AddressResponseDto {
        error = Objects.requireNonNullElse(error, false);
    }
}
