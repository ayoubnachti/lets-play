package com.ayoubnachti.lets_play.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ayoubnachti.lets_play.dtos.UserResponse;
import com.ayoubnachti.lets_play.repositories.UserRepository;

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
}