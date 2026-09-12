package com.resolvenow.auth;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class JwtUtilTest {
    @Test void generatesAndReadsToken() {
        User user = new User("agent@resolvenow.com", "encoded", "USER");
        JwtUtil jwt = new JwtUtil("ResolveNow-development-secret-key-change-me-123456", 3600000);
        assertEquals(user.getEmail(), jwt.username(jwt.generateToken(user)));
    }
}
