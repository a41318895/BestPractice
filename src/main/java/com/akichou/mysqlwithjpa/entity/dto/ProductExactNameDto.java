package com.akichou.mysqlwithjpa.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductExactNameDto(
        @NotBlank @Size(max = 64, min = 1) String productName,
        @Min(1) Integer page,
        @Min(1) Integer size) {
}
