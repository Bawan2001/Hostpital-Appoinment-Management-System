package com.hospital.auth.controller;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.LoginRequest;
import com.hospital.auth.dto.RegisterRequest;
import com.hospital.auth.dto.UserResponse;
import com.hospital.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hospital.auth.dto.OAuthTokenRequest;
import com.hospital.auth.exception.UnauthorizedException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "Endpoints for user registration, login, JWT validation, and user profiles")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account (ADMIN, DOCTOR, or PATIENT), hashes password with BCrypt, and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation error"),
            @ApiResponse(responseCode = "409", description = "Email already registered in system")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT", description = "Validates user credentials against MongoDB BCrypt password hash and returns JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or inactive account")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get user profile by ID", description = "Returns non-sensitive user profile details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved"),
            @ApiResponse(responseCode = "404", description = "User ID not found")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse response = authService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token signature and expiration")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        Boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/oauth/token")
    @Operation(summary = "OAuth 2.0 Token Endpoint (ROPC Grant)",
            description = "Issues an OAuth 2.0 access token using the Resource Owner Password Credentials (ROPC) grant type. " +
                    "Accepts grant_type='password', username (email), and password fields per RFC 6749 Section 4.3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token issued successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Unsupported grant_type"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> oauthToken(@RequestBody OAuthTokenRequest request) {
        if (!"password".equals(request.getGrantType())) {
            throw new UnauthorizedException("Unsupported grant_type. Only 'password' grant is supported.");
        }
        LoginRequest loginRequest = LoginRequest.builder()
                .email(request.getUsername())
                .password(request.getPassword())
                .build();
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
