package com.resolvenow.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthControllerTest {
    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final JwtUtil jwt = new JwtUtil("ResolveNow-development-secret-key-change-me-123456", 3600000);
    private final AuthController controller = new AuthController(users, encoder, jwt);

    @Test
    void signupNormalizesEmailBeforeSaving() {
        when(users.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("password123")).thenReturn("encoded");

        var response = controller.signup(new AuthController.Credentials(" User@Example.COM ", "password123"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(users).save(any(User.class));
    }

    @Test
    void signupDetectsEmailDuplicateIgnoringCase() {
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(new User("user@example.com", "encoded", "USER")));

        var response = controller.signup(new AuthController.Credentials("USER@example.com", "password123"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void loginNormalizesEmailBeforeLookup() {
        User user = new User("user@example.com", "encoded", "USER");
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("password123", "encoded")).thenReturn(true);

        var response = controller.login(new AuthController.Credentials(" User@Example.COM ", "password123"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(users).findByEmail("user@example.com");
    }
}
