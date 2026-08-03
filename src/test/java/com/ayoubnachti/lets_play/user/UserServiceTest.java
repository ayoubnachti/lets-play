package com.ayoubnachti.lets_play.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayoubnachti.lets_play.dtos.UserResponse;
import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.models.User;
import com.ayoubnachti.lets_play.repositories.UserRepository;
import com.ayoubnachti.lets_play.services.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findAll_returnsMappedUsers_whenUsersExist() {
        User user = User.builder()
                .id("1")
                .name("Ayoub")
                .email("ayoub@test.com")
                .password("hashed-password")
                .role(Role.USER)
                .createdAt(Instant.parse("2026-07-01T10:00:00Z"))
                .updatedAt(Instant.parse("2026-07-01T10:00:00Z"))
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("1");
        assertThat(result.get(0).email()).isEqualTo("ayoub@test.com");
        assertThat(result.get(0).role()).isEqualTo(Role.USER);
    }

    @Test
    void findAll_excludesPassword_whenMappingUser() {
        User user = User.builder()
                .id("1")
                .name("Ayoub")
                .email("ayoub@test.com")
                .password("super-secret-hash")
                .role(Role.ADMIN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.findAll();

        assertThat(result.get(0).toString()).doesNotContain("super-secret-hash");
    }

    @Test
    void findAll_returnsEmptyList_whenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.findAll();

        assertThat(result).isEmpty();
    }
}