package com.ayoubnachti.lets_play.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank
    @Size(max = 100)
    String name,

    @NotBlank
    @Email
    String email
) {}