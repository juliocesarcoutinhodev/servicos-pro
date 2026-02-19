package com.servicepro.auth.application.service.refreshtoken;

import java.time.OffsetDateTime;

public record IssuedRefreshToken(
        String token,
        OffsetDateTime expiresAt
) {
}
