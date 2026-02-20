package com.servicepro.auth.domain.gateway;

import com.servicepro.auth.domain.model.PasswordResetToken;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenGateway {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findLatestActiveByUserIdAndTokenHash(UUID userId, String tokenHash);

    void markAllAsUsedByUserId(UUID userId, OffsetDateTime usedAt);
}
