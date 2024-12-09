package com.akichou.mysqlwithjpa.entity.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductDeleteDto(
        @NotNull List<Long> productIds) {
}
