package com.ayoubnachti.lets_play.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ayoubnachti.lets_play.dtos.UserResponse;
import com.ayoubnachti.lets_play.dtos.UserUpdateRequest;
import com.ayoubnachti.lets_play.exceptions.custom.ResourceNotFoundException;
import com.ayoubnachti.lets_play.models.User;
import com.ayoubnachti.lets_play.repositories.UserRepository;
import com.ayoubnachti.lets_play.security.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public List<UserResponse> findAll() {
    return userRepository.findAll()
        .stream()
        .map(UserResponse::from)
        .toList();
  }

  public UserResponse findById(String id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", id));
    return UserResponse.from(user);
  }

  public UserResponse updateUser(String id, UserUpdateRequest request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", id));

    user.setName(request.name());
    user.setEmail(request.email());

    User saved = userRepository.save(user);
    return UserResponse.from(saved);
  }

  public void deleteUser(String id, AuthenticatedUser currentUser) {
    if (id.equals(currentUser.id())) {
      throw new AccessDeniedException("Cannot delete your own account");
    }

    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException("User", id);
    }

    userRepository.deleteById(id);
  }
}