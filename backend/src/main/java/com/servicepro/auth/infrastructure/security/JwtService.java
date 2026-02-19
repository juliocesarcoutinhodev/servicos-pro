package com.servicepro.auth.infrastructure.security;

import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.model.AccessTokenClaims;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService implements TokenGateway {

    private final String issuer;
    private final SecretKey secretKey;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;

    public JwtService(
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-ttl-seconds}") long accessTokenTtlSeconds,
            @Value("${security.jwt.refresh-token-ttl-seconds}") long refreshTokenTtlSeconds
    ) {
        this.issuer = issuer;
        this.secretKey = Keys.hmacShaKeyFor(deriveHs512Key(secret));
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    @Override
    public String generateAccessToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(accessTokenTtlSeconds);

        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getEmail())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .claim("userId", user.getId().toString())
                .claim("role", user.getRole().name())
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    @Override
    public AccessTokenClaims extractClaims(String token) {
        Claims claims = parseClaims(token);
        return new AccessTokenClaims(
                UUID.fromString(claims.get("userId", String.class)),
                claims.getSubject(),
                Role.valueOf(claims.get("role", String.class)),
                claims.getId(),
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant()
        );
    }

    @Override
    public long accessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    @Override
    public long refreshTokenTtlSeconds() {
        return refreshTokenTtlSeconds;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private byte[] deriveHs512Key(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Algoritmo SHA-512 indisponivel.", exception);
        }
    }
}
