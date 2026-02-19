package com.servicepro.auth.infrastructure.security;

import com.servicepro.auth.domain.model.AccessTokenClaims;
import com.servicepro.auth.domain.model.Role;
import java.util.UUID;

public record AuthenticatedUserPrincipal(
        UUID userId,
        String email,
        Role role
) {

    public static AuthenticatedUserPrincipal fromClaims(AccessTokenClaims claims) {
        return new AuthenticatedUserPrincipal(
                claims.userId(),
                claims.email(),
                claims.role()
        );
    }
}
