package com.akichou.mysqlwithjpa.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductDto(
        @NotBlank @Size(max = 64, min = 1) String productName,
        @Size(max = 255) String description) {
}
