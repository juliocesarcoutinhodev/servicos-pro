package com.servicepro.auth.infrastructure.security;

import com.servicepro.auth.domain.model.AccessTokenClaims;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "servicepro-test",
                "test-secret-not-used-directly-as-hs512-key",
                900,
                604800
        );
    }

    @Test
    void shouldGenerateAndValidateAccessToken() {
        User user = User.restore(
                UUID.randomUUID(),
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "argon-hash",
                Role.CLIENT,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        String token = jwtService.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();

        AccessTokenClaims claims = jwtService.extractClaims(token);
        assertThat(claims.userId()).isEqualTo(user.getId());
        assertThat(claims.email()).isEqualTo("joao@email.com");
        assertThat(claims.role()).isEqualTo(Role.CLIENT);
        assertThat(claims.jti()).isNotBlank();
        assertThat(claims.expiresAt()).isAfter(claims.issuedAt());
    }

    @Test
    void shouldGenerateUuidRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken();
        UUID parsed = UUID.fromString(refreshToken);
        assertThat(parsed).isNotNull();
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalid() {
        assertThat(jwtService.validateToken("token-invalido")).isFalse();
    }
}
