package com.ayoubnachti.lets_play.dtos;

import com.ayoubnachti.lets_play.enums.Role;

public record UserResponse(
    String id,
    String name,
    String email,
    Role role
) {}