package com.ayoubnachti.lets_play.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ayoubnachti.lets_play.services.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validToken_setsAuthenticationOnSecurityContext() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.parseClaims("valid-token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("test@example.com");
        when(claims.get("userId", String.class)).thenReturn("user-123");
        when(claims.get("role", String.class)).thenReturn("USER");

        filter.doFilterInternal(request, response, filterChain);

        var auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();

        var principal = (AuthenticatedUser) auth.getPrincipal();
        assertThat(principal.id()).isEqualTo("user-123");
        assertThat(principal.email()).isEqualTo("test@example.com");
        assertThat(principal.role()).isEqualTo("USER");
        assertThat(auth.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void missingAuthorizationHeader_leavesContextEmpty_andContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void headerWithoutBearerPrefix_leavesContextEmpty_andContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void invalidToken_leavesContextEmpty_andContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad-token");
        when(jwtService.parseClaims("bad-token")).thenThrow(new ExpiredJwtException(null, null, "expired"));
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/products");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}