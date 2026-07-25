package com.ayoubnachti.lets_play.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.models.User;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidationTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void setUp() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void tearDown() {
    factory.close();
  }

  private User.UserBuilder validUserBuilder() {
    return User.builder()
        .name("Ayoub Nachti")
        .email("ayoub@example.com")
        .password("$2a$10$hashedvaluehere")
        .role(Role.USER);
  }

  @Test
  void validUser_hasNoViolations() {
    User user = validUserBuilder().build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertTrue(violations.isEmpty());
  }

  @Test
  void blankName_isRejected() {
    User user = validUserBuilder().name("").build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertEquals(1, violations.size());
  }

  @Test
  void blankEmail_isRejected() {
    User user = validUserBuilder().email("").build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    violations.forEach(v -> System.out.println(v.getPropertyPath() + " -> " + v.getMessage()));

    assertEquals(1, violations.size());
  }

  @Test
  void malformedEmail_isRejected() {
    User user = validUserBuilder().email("not-an-email").build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertEquals(1, violations.size());
  }

  @Test
  void blankPassword_isRejected() {
    User user = validUserBuilder().password("").build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertEquals(1, violations.size());
  }

  @Test
  void nullRole_isRejected() {
    User user = validUserBuilder().role(null).build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertEquals(1, violations.size());
  }
}