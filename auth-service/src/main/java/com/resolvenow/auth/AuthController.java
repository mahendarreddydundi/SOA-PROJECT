package com.resolvenow.auth;

import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/auth")
public class AuthController {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtUtil jwt;
    public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) { this.users = users; this.encoder = encoder; this.jwt = jwt; }
    public record Credentials(String email, String password) { }
    public record TokenResponse(String token) { }
    @PostMapping("/signup") public ResponseEntity<?> signup(@RequestBody Credentials input) {
        if (users.findByEmail(input.email()).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        users.save(new User(input.email(), encoder.encode(input.password()), "USER")); return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/login") public ResponseEntity<TokenResponse> login(@RequestBody Credentials input) {
        return users.findByEmail(input.email()).filter(user -> encoder.matches(input.password(), user.getPassword())).map(user -> ResponseEntity.ok(new TokenResponse(jwt.generateToken(user)))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
