package com.hospital.auth.service.impl;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.LoginRequest;
import com.hospital.auth.dto.RegisterRequest;
import com.hospital.auth.dto.UserResponse;
import com.hospital.auth.entity.User;
import com.hospital.auth.exception.EmailAlreadyExistsException;
import com.hospital.auth.exception.ResourceNotFoundException;
import com.hospital.auth.exception.UnauthorizedException;
import com.hospital.auth.repository.UserRepository;
import com.hospital.auth.security.JwtTokenProvider;
import com.hospital.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        String phone = (request.getPhone() != null && !request.getPhone().isBlank()) ? request.getPhone() : "+94771234567";

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(phone)
                .role(request.getRole())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationSeconds())
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new UnauthorizedException("Account is inactive or suspended");
        }

        String token = jwtTokenProvider.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationSeconds())
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserResponse(user);
    }

    @Override
    public Boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
