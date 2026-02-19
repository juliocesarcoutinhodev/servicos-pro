package com.servicepro.auth.application.usecase.refresh;

import com.servicepro.auth.application.service.refreshtoken.IssuedRefreshToken;
import com.servicepro.auth.application.service.refreshtoken.RefreshTokenService;
import com.servicepro.auth.application.usecase.login.TokenPair;
import com.servicepro.auth.domain.exception.TokenRevokedException;
import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.RefreshToken;
import com.servicepro.auth.domain.model.User;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshUseCaseImpl implements RefreshUseCase {

    private final RefreshTokenGateway refreshTokenGateway;
    private final RefreshTokenHasher refreshTokenHasher;
    private final RefreshTokenCacheGateway refreshTokenCacheGateway;
    private final UserGateway userGateway;
    private final TokenGateway tokenGateway;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public RefreshResult execute(RefreshCommand command) {
        String rawRefreshToken = command.rawRefreshToken();
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new TokenRevokedException();
        }

        String tokenHash = refreshTokenHasher.hash(rawRefreshToken);
        RefreshToken refreshToken = refreshTokenGateway.findByTokenHash(tokenHash)
                .orElseThrow(TokenRevokedException::new);

        if (refreshToken.isRevoked()) {
            revokeAllAndClearCache(refreshToken.getUserId());
            throw new TokenRevokedException();
        }

        if (refreshToken.isExpired(OffsetDateTime.now(ZoneOffset.UTC))) {
            revokeSingleToken(rawRefreshToken, refreshToken);
            throw new TokenRevokedException();
        }

        String cacheTokenHash = refreshTokenCacheGateway.get(refreshToken.getUserId(), rawRefreshToken)
                .orElse(null);
        if (!refreshToken.getTokenHash().equals(cacheTokenHash)) {
            revokeAllAndClearCache(refreshToken.getUserId());
            throw new TokenRevokedException();
        }

        revokeSingleToken(rawRefreshToken, refreshToken);

        User user = userGateway.findById(refreshToken.getUserId())
                .orElseThrow(TokenRevokedException::new);
        String accessToken = tokenGateway.generateAccessToken(user);
        IssuedRefreshToken issuedRefreshToken = refreshTokenService.create(user.getId());

        TokenPair tokenPair = new TokenPair(accessToken, tokenGateway.accessTokenTtlSeconds());
        return new RefreshResult(tokenPair, issuedRefreshToken.token());
    }

    private void revokeSingleToken(String rawRefreshToken, RefreshToken refreshToken) {
        if (!refreshToken.isRevoked()) {
            refreshTokenGateway.save(refreshToken.revoke());
        }
        refreshTokenCacheGateway.evict(refreshToken.getUserId(), rawRefreshToken);
    }

    private void revokeAllAndClearCache(UUID userId) {
        refreshTokenGateway.revokeAllByUserId(userId);
        refreshTokenCacheGateway.evictAll(userId);
    }
}
