package com.hospital.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth 2.0 Resource Owner Password Credentials (ROPC) grant request.
 * Follows RFC 6749 Section 4.3.2 token request format.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthTokenRequest {

    @JsonProperty("grant_type")
    private String grantType;

    private String username;

    private String password;
}
