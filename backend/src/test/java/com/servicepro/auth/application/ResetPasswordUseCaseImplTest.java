package com.servicepro.auth.application;

import com.servicepro.auth.application.usecase.resetpassword.ResetPasswordCommand;
import com.servicepro.auth.application.usecase.resetpassword.ResetPasswordUseCase;
import com.servicepro.auth.application.usecase.resetpassword.ResetPasswordUseCaseImpl;
import com.servicepro.auth.domain.exception.InvalidPasswordResetTokenException;
import com.servicepro.auth.domain.exception.InvalidSignupPasswordException;
import com.servicepro.auth.domain.gateway.PasswordHasher;
import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.PasswordResetToken;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetPasswordUseCaseImplTest {

    @Mock
    private PasswordResetTokenGateway passwordResetTokenGateway;

    @Mock
    private RefreshTokenHasher refreshTokenHasher;

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private RefreshTokenGateway refreshTokenGateway;

    @Mock
    private RefreshTokenCacheGateway refreshTokenCacheGateway;

    private ResetPasswordUseCase resetPasswordUseCase;

    @BeforeEach
    void setUp() {
        resetPasswordUseCase = new ResetPasswordUseCaseImpl(
                passwordResetTokenGateway,
                refreshTokenHasher,
                userGateway,
                passwordHasher,
                refreshTokenGateway,
                refreshTokenCacheGateway
        );
    }

    @Test
    void shouldResetPasswordWhenTokenIsValid() {
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        PasswordResetToken resetToken = PasswordResetToken.restore(
                UUID.randomUUID(),
                userId,
                "token-hash",
                now.plusMinutes(20),
                null,
                now.minusMinutes(1)
        );

        User user = User.restore(
                userId,
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "old-hash",
                Role.CLIENT,
                true,
                now.minusDays(1),
                now.minusDays(1)
        );

        when(userGateway.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(refreshTokenHasher.hash("123456")).thenReturn("token-hash");
        when(passwordResetTokenGateway.findLatestActiveByUserIdAndTokenHash(userId, "token-hash"))
                .thenReturn(Optional.of(resetToken));
        when(passwordHasher.hash("NovaSenha123")).thenReturn("new-hash");
        when(passwordResetTokenGateway.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        resetPasswordUseCase.execute(new ResetPasswordCommand("joao@email.com", "123456", "NovaSenha123"));

        then(userGateway).should().updatePasswordHash(userId, "new-hash");
        then(refreshTokenGateway).should().revokeAllByUserId(userId);
        then(refreshTokenCacheGateway).should().evictAll(userId);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        then(passwordResetTokenGateway).should().save(tokenCaptor.capture());
        then(userGateway).should().findByEmail("joao@email.com");

        PasswordResetToken persistedToken = tokenCaptor.getValue();
        then(passwordResetTokenGateway).should().findLatestActiveByUserIdAndTokenHash(userId, "token-hash");
        then(passwordHasher).should().hash("NovaSenha123");

        assertThat(persistedToken.isUsed()).isTrue();
    }

    @Test
    void shouldThrowWhenResetTokenIsInvalid() {
        UUID userId = UUID.randomUUID();
        User user = User.restore(
                userId,
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "old-hash",
                Role.CLIENT,
                true,
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(1),
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(1)
        );

        when(userGateway.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(refreshTokenHasher.hash("123456")).thenReturn("token-hash");
        when(passwordResetTokenGateway.findLatestActiveByUserIdAndTokenHash(userId, "token-hash")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordUseCase.execute(new ResetPasswordCommand("joao@email.com", "123456", "NovaSenha123")))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessageContaining("Codigo de redefinicao");

        then(passwordHasher).shouldHaveNoInteractions();
        then(refreshTokenGateway).shouldHaveNoInteractions();
        then(refreshTokenCacheGateway).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowWhenPasswordIsInvalid() {
        assertThatThrownBy(() -> resetPasswordUseCase.execute(new ResetPasswordCommand("joao@email.com", "123456", "123")))
                .isInstanceOf(InvalidSignupPasswordException.class)
                .hasMessageContaining("entre 8 e 72");

        then(refreshTokenHasher).shouldHaveNoInteractions();
        then(passwordResetTokenGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(passwordHasher).shouldHaveNoInteractions();
    }
}
