package com.ayoubnachti.lets_play.services;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ayoubnachti.lets_play.dtos.LoginRequest;
import com.ayoubnachti.lets_play.dtos.RegisterRequest;
import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.models.User;
import com.ayoubnachti.lets_play.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * AuthService
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService JwtService;

  public User register(RegisterRequest request) {
    User user = User.builder()
        .name(request.name())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .role(Role.USER)
        .build();

    return userRepository.save(user);
  }

  public String login(LoginRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    return JwtService.generateToken(user);
  }
}