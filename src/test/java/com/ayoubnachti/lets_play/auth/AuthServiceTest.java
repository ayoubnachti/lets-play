package com.ayoubnachti.lets_play.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ayoubnachti.lets_play.dtos.LoginRequest;
import com.ayoubnachti.lets_play.dtos.RegisterRequest;
import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.models.User;
import com.ayoubnachti.lets_play.repositories.UserRepository;
import com.ayoubnachti.lets_play.services.AuthService;
import com.ayoubnachti.lets_play.services.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_hashesPasswordAndDefaultsRoleToUser() {
        RegisterRequest request = new RegisterRequest("Test user", "test@example.com", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(request);

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getPassword()).isEqualTo("hashedPassword");
        assertThat(result.getRole()).isEqualTo(Role.USER);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("test@example.com", "plainPassword123");
        User user = User.builder()
                .email("test@example.com")
                .password("hashedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        String token = authService.login(request);

        assertThat(token).isEqualTo("fake-jwt-token");
    }

    @Test
    void login_throwsBadCredentials_whenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        User user = User.builder()
                .email("test@example.com")
                .password("hashedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void login_throwsBadCredentials_whenEmailNotFound() {
        LoginRequest request = new LoginRequest("nobody@example.com", "somePassword");

        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }
}