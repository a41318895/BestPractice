package com.akichou.mysqlwithjpa.entity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductUpdateDto(
        @NotNull Long id,
        @Size(max = 64, min = 1) String productName,
        @Size(max = 255) String description) {
}
