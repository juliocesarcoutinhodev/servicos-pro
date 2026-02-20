package com.servicepro.auth.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class PasswordResetToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final OffsetDateTime expiresAt;
    private final OffsetDateTime usedAt;
    private final OffsetDateTime createdAt;

    private PasswordResetToken(
            UUID id,
            UUID userId,
            String tokenHash,
            OffsetDateTime expiresAt,
            OffsetDateTime usedAt,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId e obrigatorio.");
        this.tokenHash = validateTokenHash(tokenHash);
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt e obrigatorio.");
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }

    public static PasswordResetToken issue(UUID userId, String tokenHash, OffsetDateTime expiresAt) {
        return new PasswordResetToken(null, userId, tokenHash, expiresAt, null, null);
    }

    public static PasswordResetToken restore(
            UUID id,
            UUID userId,
            String tokenHash,
            OffsetDateTime expiresAt,
            OffsetDateTime usedAt,
            OffsetDateTime createdAt
    ) {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, usedAt, createdAt);
    }

    public PasswordResetToken markAsUsed(OffsetDateTime usageTime) {
        if (isUsed()) {
            return this;
        }
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, usageTime, createdAt);
    }

    public boolean isExpired(OffsetDateTime now) {
        return expiresAt.isBefore(now) || expiresAt.isEqual(now);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public OffsetDateTime getUsedAt() {
        return usedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    private static String validateTokenHash(String tokenHash) {
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("tokenHash e obrigatorio.");
        }
        return tokenHash;
    }
}
