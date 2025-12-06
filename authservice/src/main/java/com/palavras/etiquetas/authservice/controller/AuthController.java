package com.palavras.etiquetas.authservice.controller;

import com.palavras.etiquetas.authservice.dto.LoginRequest;
import com.palavras.etiquetas.authservice.dto.LoginResponse;
import com.palavras.etiquetas.authservice.dto.ValidateResponse;
import com.palavras.etiquetas.authservice.entity.User;
import com.palavras.etiquetas.authservice.repository.UserRepository;
import com.palavras.etiquetas.authservice.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Note: Plain text password comparison is used for simplicity in this skeleton demo.
        // In production, use proper password hashing (bcrypt, Argon2, etc.)
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail(), user.getProfile());
                    return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getProfile()));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(new ValidateResponse(false, null, null));
        }

        String token = authHeader.substring(7);
        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            String profile = jwtUtil.extractProfile(token);
            return ResponseEntity.ok(new ValidateResponse(true, email, profile));
        }

        return ResponseEntity.ok(new ValidateResponse(false, null, null));
    }
}
