package com.servicepro.auth.application;

import com.servicepro.auth.application.service.refreshtoken.IssuedRefreshToken;
import com.servicepro.auth.application.service.refreshtoken.RefreshTokenService;
import com.servicepro.auth.application.usecase.refresh.RefreshCommand;
import com.servicepro.auth.application.usecase.refresh.RefreshResult;
import com.servicepro.auth.application.usecase.refresh.RefreshUseCase;
import com.servicepro.auth.application.usecase.refresh.RefreshUseCaseImpl;
import com.servicepro.auth.domain.exception.TokenRevokedException;
import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.RefreshToken;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshUseCaseImplTest {

    @Mock
    private RefreshTokenGateway refreshTokenGateway;

    @Mock
    private RefreshTokenHasher refreshTokenHasher;

    @Mock
    private RefreshTokenCacheGateway refreshTokenCacheGateway;

    @Mock
    private UserGateway userGateway;

    @Mock
    private TokenGateway tokenGateway;

    @Mock
    private RefreshTokenService refreshTokenService;

    private RefreshUseCase refreshUseCase;

    @BeforeEach
    void setUp() {
        refreshUseCase = new RefreshUseCaseImpl(
                refreshTokenGateway,
                refreshTokenHasher,
                refreshTokenCacheGateway,
                userGateway,
                tokenGateway,
                refreshTokenService
        );
    }

    @Test
    void shouldRotateTokenWhenRefreshTokenIsValid() {
        UUID userId = UUID.randomUUID();
        String rawRefreshToken = "old-refresh-token";
        String tokenHash = "hash-old-refresh-token";
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        RefreshToken currentToken = RefreshToken.restore(
                UUID.randomUUID(),
                userId,
                tokenHash,
                now.plusHours(1),
                false,
                now.minusHours(1)
        );
        User user = User.restore(
                userId,
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "argon-hash",
                Role.CLIENT,
                true,
                now.minusDays(1),
                now.minusDays(1)
        );

        when(refreshTokenHasher.hash(rawRefreshToken)).thenReturn(tokenHash);
        when(refreshTokenGateway.findByTokenHash(tokenHash)).thenReturn(Optional.of(currentToken));
        when(refreshTokenCacheGateway.get(userId, rawRefreshToken)).thenReturn(Optional.of(tokenHash));
        when(userGateway.findById(userId)).thenReturn(Optional.of(user));
        when(tokenGateway.generateAccessToken(user)).thenReturn("new-access-token");
        when(tokenGateway.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokenService.create(userId))
                .thenReturn(new IssuedRefreshToken("new-refresh-token", now.plusDays(7)));

        RefreshResult result = refreshUseCase.execute(new RefreshCommand(rawRefreshToken));

        assertThat(result.tokenPair().accessToken()).isEqualTo("new-access-token");
        assertThat(result.tokenPair().expiresIn()).isEqualTo(900L);
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");

        then(refreshTokenGateway).should().save(argThat(token -> token.isRevoked() && token.getId().equals(currentToken.getId())));
        then(refreshTokenCacheGateway).should().evict(userId, rawRefreshToken);
        then(refreshTokenService).should().create(userId);
    }

    @Test
    void shouldRevokeAllTokensWhenRefreshTokenIsReused() {
        UUID userId = UUID.randomUUID();
        String rawRefreshToken = "replayed-refresh-token";
        String tokenHash = "hash-replayed-refresh-token";

        RefreshToken revokedToken = RefreshToken.restore(
                UUID.randomUUID(),
                userId,
                tokenHash,
                OffsetDateTime.now(ZoneOffset.UTC).plusHours(1),
                true,
                OffsetDateTime.now(ZoneOffset.UTC).minusHours(1)
        );

        when(refreshTokenHasher.hash(rawRefreshToken)).thenReturn(tokenHash);
        when(refreshTokenGateway.findByTokenHash(tokenHash)).thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> refreshUseCase.execute(new RefreshCommand(rawRefreshToken)))
                .isInstanceOf(TokenRevokedException.class)
                .hasMessageContaining("Refresh token");

        then(refreshTokenGateway).should().revokeAllByUserId(userId);
        then(refreshTokenCacheGateway).should().evictAll(userId);
        then(refreshTokenService).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowWhenRefreshTokenIsExpired() {
        UUID userId = UUID.randomUUID();
        String rawRefreshToken = "expired-refresh-token";
        String tokenHash = "hash-expired-refresh-token";

        RefreshToken expiredToken = RefreshToken.restore(
                UUID.randomUUID(),
                userId,
                tokenHash,
                OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(1),
                false,
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(7)
        );

        when(refreshTokenHasher.hash(rawRefreshToken)).thenReturn(tokenHash);
        when(refreshTokenGateway.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> refreshUseCase.execute(new RefreshCommand(rawRefreshToken)))
                .isInstanceOf(TokenRevokedException.class)
                .hasMessageContaining("Refresh token");

        then(refreshTokenGateway).should().save(argThat(token -> token.isRevoked() && token.getId().equals(expiredToken.getId())));
        then(refreshTokenCacheGateway).should().evict(userId, rawRefreshToken);
        then(refreshTokenGateway).shouldHaveNoMoreInteractions();
        then(refreshTokenService).shouldHaveNoInteractions();
    }
}
