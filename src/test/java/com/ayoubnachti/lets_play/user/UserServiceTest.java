package com.ayoubnachti.lets_play.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.ayoubnachti.lets_play.dtos.UserResponse;
import com.ayoubnachti.lets_play.dtos.UserUpdateRequest;
import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.exceptions.custom.ResourceNotFoundException;
import com.ayoubnachti.lets_play.models.User;
import com.ayoubnachti.lets_play.repositories.UserRepository;
import com.ayoubnachti.lets_play.security.AuthenticatedUser;
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

    @Test
    void findById_returnsUser_whenUserExists() {
        User user = User.builder()
                .id("1")
                .name("Ayoub")
                .email("ayoub@test.com")
                .password("hashed-password")
                .role(Role.USER)
                .createdAt(Instant.parse("2026-07-01T10:00:00Z"))
                .updatedAt(Instant.parse("2026-07-01T10:00:00Z"))
                .build();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        UserResponse result = userService.findById("1");

        assertThat(result.id()).isEqualTo("1");
        assertThat(result.email()).isEqualTo("ayoub@test.com");
    }

    @Test
    void findById_throwsResourceNotFound_whenUserDoesNotExist() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateUser_updatesAndReturnsUser_whenUserExists() {
        User existing = User.builder()
                .id("1")
                .name("Old Name")
                .email("old@test.com")
                .password("hashed-password")
                .role(Role.USER)
                .createdAt(Instant.parse("2026-07-01T10:00:00Z"))
                .updatedAt(Instant.parse("2026-07-01T10:00:00Z"))
                .build();

        UserUpdateRequest request = new UserUpdateRequest("New Name", "new@test.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        UserResponse result = userService.updateUser("1", request);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.email()).isEqualTo("new@test.com");
    }

    @Test
    void updateUser_throwsResourceNotFound_whenUserDoesNotExist() {
        UserUpdateRequest request = new UserUpdateRequest("New Name", "new@test.com");

        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser("missing", request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deletesUser_whenUserExistsAndNotSelf() {
        AuthenticatedUser currentUser = new AuthenticatedUser("admin-1", "admin@test.com", "ADMIN");

        when(userRepository.existsById("target-1")).thenReturn(true);

        userService.deleteUser("target-1", currentUser);

        verify(userRepository).deleteById("target-1");
    }

    @Test
    void deleteUser_throwsResourceNotFound_whenUserDoesNotExist() {
        AuthenticatedUser currentUser = new AuthenticatedUser("admin-1", "admin@test.com", "ADMIN");

        when(userRepository.existsById("missing")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser("missing", currentUser))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUser_throwsAccessDenied_whenDeletingSelf() {
        AuthenticatedUser currentUser = new AuthenticatedUser("admin-1", "admin@test.com", "ADMIN");

        assertThatThrownBy(() -> userService.deleteUser("admin-1", currentUser))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(userRepository);
    }
}