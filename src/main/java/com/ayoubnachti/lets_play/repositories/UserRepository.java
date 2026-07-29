package com.ayoubnachti.lets_play.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ayoubnachti.lets_play.models.User;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByEmail(String email);
}
