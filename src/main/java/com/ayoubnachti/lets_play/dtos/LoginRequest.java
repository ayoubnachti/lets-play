package com.ayoubnachti.lets_play.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank
    String email,

    @NotBlank
    String password
) {}
