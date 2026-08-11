package com.hospital.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hospital.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth 2.0 compliant token response following RFC 6749 Section 5.1.
 * Returns access_token, token_type, and expires_in alongside user profile data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    @JsonProperty("access_token")
    private String token;

    @JsonProperty("token_type")
    @Builder.Default
    private String type = "Bearer";

    @JsonProperty("expires_in")
    private long expiresIn;

    private String id;
    private String name;
    private String email;
    private Role role;
}
