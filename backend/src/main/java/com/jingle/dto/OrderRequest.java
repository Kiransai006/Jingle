package com.jingle.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotBlank String customerName,
        @NotBlank String phone,
        @NotBlank String address,
        @NotEmpty List<@Valid Line> items) {

    public record Line(@NotNull Long menuItemId, @Min(1) int quantity) {
    }
}
