package com.ayoubnachti.lets_play.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayoubnachti.lets_play.dtos.RegisterRequest;
import com.ayoubnachti.lets_play.dtos.UserResponse;
import com.ayoubnachti.lets_play.models.User;
import com.ayoubnachti.lets_play.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User created = authService.register(request);
        UserResponse body = new UserResponse(created.getId(), created.getName(), created.getEmail(), created.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}