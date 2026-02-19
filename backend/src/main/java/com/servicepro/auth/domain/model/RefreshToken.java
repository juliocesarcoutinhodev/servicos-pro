package com.servicepro.auth.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final OffsetDateTime expiresAt;
    private final boolean revoked;
    private final OffsetDateTime createdAt;

    private RefreshToken(
            UUID id,
            UUID userId,
            String tokenHash,
            OffsetDateTime expiresAt,
            boolean revoked,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId e obrigatorio.");
        this.tokenHash = validateTokenHash(tokenHash);
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt e obrigatorio.");
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshToken issue(UUID userId, String tokenHash, OffsetDateTime expiresAt) {
        return new RefreshToken(null, userId, tokenHash, expiresAt, false, null);
    }

    public static RefreshToken restore(
            UUID id,
            UUID userId,
            String tokenHash,
            OffsetDateTime expiresAt,
            boolean revoked,
            OffsetDateTime createdAt
    ) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revoked, createdAt);
    }

    public RefreshToken revoke() {
        return new RefreshToken(id, userId, tokenHash, expiresAt, true, createdAt);
    }

    public boolean isExpired(OffsetDateTime now) {
        return expiresAt.isBefore(now) || expiresAt.isEqual(now);
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

    public boolean isRevoked() {
        return revoked;
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
