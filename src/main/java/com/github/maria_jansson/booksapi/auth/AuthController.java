package com.github.maria_jansson.booksapi.auth;

import com.github.maria_jansson.booksapi.dto.AuthRequestDTO;
import com.github.maria_jansson.booksapi.dto.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT token.")
    @PostMapping("/register")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "409", description = "Username already taken")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody AuthRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(data));
    }

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token.")
    @PostMapping("/login")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }
}
