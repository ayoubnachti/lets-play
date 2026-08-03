package com.ayoubnachti.lets_play.dtos;

import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.models.User;

public record UserResponse(
		String id,
		String name,
		String email,
		Role role) {
	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
	}
}