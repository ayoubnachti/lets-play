package com.ayoubnachti.lets_play.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.models.User;
import com.ayoubnachti.lets_play.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void run_seedsAdmin_whenEmailAndPasswordProvidedAndNoExistingUser() {
        AdminSeeder seeder = new AdminSeeder(userRepository, passwordEncoder, "admin@test.com", "supersecret");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("supersecret")).thenReturn("hashed");

        seeder.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("admin@test.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void run_skipsSeed_whenAdminAlreadyExists() {
        AdminSeeder seeder = new AdminSeeder(userRepository, passwordEncoder, "admin@test.com", "supersecret");
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(User.builder().build()));

        seeder.run();

        verify(userRepository, never()).save(any());
    }

    @Test
    void run_skipsSeed_whenEnvVarsNotSet() {
        AdminSeeder seeder = new AdminSeeder(userRepository, passwordEncoder, "", "");

        seeder.run();

        verify(userRepository, never()).findByEmail(any());
        verifyNoInteractions(passwordEncoder);
    }
}