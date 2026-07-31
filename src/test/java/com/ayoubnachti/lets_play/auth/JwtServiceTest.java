package com.ayoubnachti.lets_play.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.models.User;
import com.ayoubnachti.lets_play.services.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "secret",
        "test-secret-test-secret-test-secret-32chars");
    ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
  }

  @Test
  void generateThenParse_roundTripsClaimsCorrectly() {
    User user = User.builder().email("test@example.com").role(Role.USER).build();

    String token = jwtService.generateToken(user);
    Claims claims = jwtService.parseClaims(token);

    assertThat(claims.getSubject()).isEqualTo("test@example.com");
    assertThat(claims.get("role", String.class)).isEqualTo("USER");
  }

  @Test
  void parseClaims_expiredToken_throwsExpiredJwtException() {
    ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L); // already expired
    User user = User.builder().email("test@exanmple.com").role(Role.USER).build();
    String token = jwtService.generateToken(user);

    assertThrows(ExpiredJwtException.class, () -> jwtService.parseClaims(token));
  }

  @Test
  void parseClaims_tamperedToken_throwsSignatureException() {
    User user = User.builder().email("test@example.com").role(Role.USER).build();
    String token = jwtService.generateToken(user) + "tampered";

    assertThrows(JwtException.class, () -> jwtService.parseClaims(token));
  }
}