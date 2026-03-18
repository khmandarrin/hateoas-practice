package dev.products.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String tokenType;
    private String accessToken;
    private long expiresIn;
}
