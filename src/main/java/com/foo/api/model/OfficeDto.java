package com.foo.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record OfficeDto(
        @Positive
        @NotNull
        Integer code,
        @NotBlank
        @Size(max = 50)
        String city,
        @NotBlank
        @Size(max = 50)
        String country) {
}