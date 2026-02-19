package com.servicepro.auth.application.service.refreshtoken;

import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.model.RefreshToken;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenGateway refreshTokenGateway;
    private final RefreshTokenHasher refreshTokenHasher;
    private final RefreshTokenCacheGateway refreshTokenCacheGateway;
    private final TokenGateway tokenGateway;

    @Override
    @Transactional
    public IssuedRefreshToken create(UUID userId) {
        String rawToken = tokenGateway.generateRefreshToken();
        String tokenHash = refreshTokenHasher.hash(rawToken);
        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC)
                .plusSeconds(tokenGateway.refreshTokenTtlSeconds());

        RefreshToken refreshToken = RefreshToken.issue(userId, tokenHash, expiresAt);
        RefreshToken savedToken = refreshTokenGateway.save(refreshToken);

        refreshTokenCacheGateway.put(
                savedToken.getUserId(),
                rawToken,
                savedToken.getTokenHash(),
                Duration.ofSeconds(tokenGateway.refreshTokenTtlSeconds())
        );

        return new IssuedRefreshToken(rawToken, savedToken.getExpiresAt());
    }

    @Override
    @Transactional
    public void revoke(String rawRefreshToken) {
        String tokenHash = refreshTokenHasher.hash(rawRefreshToken);
        refreshTokenGateway.findByTokenHash(tokenHash)
                .ifPresent(refreshToken -> {
                    if (!refreshToken.isRevoked()) {
                        refreshTokenGateway.save(refreshToken.revoke());
                    }
                    refreshTokenCacheGateway.evict(refreshToken.getUserId(), rawRefreshToken);
                });
    }
}
