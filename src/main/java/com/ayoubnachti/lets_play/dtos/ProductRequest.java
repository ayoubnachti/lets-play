package com.ayoubnachti.lets_play.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductRequest(
    @NotBlank
    @Size(max = 100)
    String name,

    @Size(max = 1000)
    String description,

    @NotNull
    @PositiveOrZero
    Double price
) {}