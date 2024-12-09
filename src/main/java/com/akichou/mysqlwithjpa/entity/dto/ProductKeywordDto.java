package com.akichou.mysqlwithjpa.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProductKeywordDto(
        @NotBlank String keyword,
        @Min(1) Integer page,
        @Min(1) Integer size) {
}
