package com.ayoubnachti.lets_play.security;

public record AuthenticatedUser(String id, String email, String role) {
}