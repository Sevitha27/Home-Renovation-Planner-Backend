package com.lowes.service;

import com.lowes.entity.User;
import com.lowes.entity.enums.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        user = new User();
        user.setExposedId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setRole(Role.CUSTOMER);
    }

    @Test
    void testGenerateAndValidateAccessToken() {
        String token = jwtService.generateAccessToken(user);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals(user.getEmail(), jwtService.extractEmail(token));
        assertTrue(jwtService.isAccessToken(token));
        assertFalse(jwtService.isRefreshToken(token));
    }

    @Test
    void testGenerateAndValidateRefreshToken() {
        String token = jwtService.generateRefreshToken(user);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals(user.getEmail(), jwtService.extractEmail(token));
        assertFalse(jwtService.isAccessToken(token));
        assertTrue(jwtService.isRefreshToken(token));
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtService.validateToken(invalidToken));
        assertThrows(Exception.class, () -> jwtService.extractEmail(invalidToken));
    }

    @Test
    void testGetClaims() {
        String token = jwtService.generateAccessToken(user);
        Claims claims = jwtService.getClaims(token);
        assertEquals(user.getEmail(), claims.get("email"));
        assertEquals(user.getRole().name(), claims.get("role").toString());
        assertEquals("access", claims.get("type"));
    }
} 