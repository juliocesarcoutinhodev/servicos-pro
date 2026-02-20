package com.servicepro.auth.application;

import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordCommand;
import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordUseCase;
import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordUseCaseImpl;
import com.servicepro.auth.domain.gateway.AccountNotificationGateway;
import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.PasswordResetToken;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.infrastructure.config.AuthLinksProperties;
import com.servicepro.auth.infrastructure.config.AuthPasswordResetProperties;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private TokenGateway tokenGateway;

    @Mock
    private RefreshTokenHasher refreshTokenHasher;

    @Mock
    private PasswordResetTokenGateway passwordResetTokenGateway;

    @Mock
    private AccountNotificationGateway accountNotificationGateway;

    private ForgotPasswordUseCase forgotPasswordUseCase;

    @BeforeEach
    void setUp() {
        AuthPasswordResetProperties resetProperties = new AuthPasswordResetProperties();
        resetProperties.setTokenTtlSeconds(1800);

        AuthLinksProperties linksProperties = new AuthLinksProperties();
        linksProperties.setResetPasswordUrl("https://app.servicepro.com/reset-password?token=%s");

        forgotPasswordUseCase = new ForgotPasswordUseCaseImpl(
                userGateway,
                tokenGateway,
                refreshTokenHasher,
                passwordResetTokenGateway,
                resetProperties,
                linksProperties,
                accountNotificationGateway
        );
    }

    @Test
    void shouldCreateResetTokenAndSendEmailWhenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = User.restore(
                userId,
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "argon-hash",
                Role.CLIENT,
                true,
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(1),
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(1)
        );

        when(userGateway.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(tokenGateway.generateRefreshToken()).thenReturn("raw-reset-token");
        when(refreshTokenHasher.hash("raw-reset-token")).thenReturn("hash-reset-token");
        when(passwordResetTokenGateway.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        forgotPasswordUseCase.execute(new ForgotPasswordCommand("JOAO@email.com"));

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        then(passwordResetTokenGateway).should().save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getUserId()).isEqualTo(userId);
        assertThat(savedToken.getTokenHash()).isEqualTo("hash-reset-token");
        assertThat(savedToken.isUsed()).isFalse();

        then(accountNotificationGateway).should().sendPasswordResetEmail(
                user,
                "https://app.servicepro.com/reset-password?token=raw-reset-token",
                savedToken.getExpiresAt()
        );
    }

    @Test
    void shouldIgnoreForgotPasswordWhenEmailDoesNotExist() {
        when(userGateway.findByEmail(anyString())).thenReturn(Optional.empty());

        forgotPasswordUseCase.execute(new ForgotPasswordCommand("desconhecido@email.com"));

        then(tokenGateway).shouldHaveNoInteractions();
        then(refreshTokenHasher).shouldHaveNoInteractions();
        then(passwordResetTokenGateway).shouldHaveNoInteractions();
        then(accountNotificationGateway).shouldHaveNoInteractions();
    }
}
