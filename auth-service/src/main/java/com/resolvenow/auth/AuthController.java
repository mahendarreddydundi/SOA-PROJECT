package com.resolvenow.auth;

import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

@RestController @RequestMapping("/auth")
public class AuthController {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtUtil jwt;
    public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) { this.users = users; this.encoder = encoder; this.jwt = jwt; }
    public record Credentials(@NotBlank @Email String email, @NotBlank @Size(min = 8, max = 128) String password) { }
    public record TokenResponse(String token) { }
    @PostMapping("/signup") public ResponseEntity<?> signup(@Valid @RequestBody Credentials input) {
        String email = normalizeEmail(input.email());
        if (users.findByEmail(email).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        users.save(new User(email, encoder.encode(input.password()), "USER")); return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/login") public ResponseEntity<TokenResponse> login(@Valid @RequestBody Credentials input) {
        return users.findByEmail(normalizeEmail(input.email())).filter(user -> encoder.matches(input.password(), user.getPassword())).map(user -> ResponseEntity.ok(new TokenResponse(jwt.generateToken(user)))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
    private String normalizeEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}
