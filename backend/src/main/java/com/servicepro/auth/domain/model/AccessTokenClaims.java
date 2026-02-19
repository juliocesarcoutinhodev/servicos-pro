package com.servicepro.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AccessTokenClaims(
        UUID userId,
        String email,
        Role role,
        String jti,
        Instant issuedAt,
        Instant expiresAt
) {
}
