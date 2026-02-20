package com.servicepro.auth.application;

import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordCommand;
import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordUseCase;
import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordUseCaseImpl;
import com.servicepro.auth.domain.gateway.AccountNotificationGateway;
import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.PasswordResetToken;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

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

        forgotPasswordUseCase = new ForgotPasswordUseCaseImpl(
                userGateway,
                refreshTokenHasher,
                passwordResetTokenGateway,
                resetProperties,
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
        when(refreshTokenHasher.hash(anyString()))
                .thenAnswer(invocation -> "hash-" + invocation.getArgument(0, String.class));
        when(passwordResetTokenGateway.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        forgotPasswordUseCase.execute(new ForgotPasswordCommand("JOAO@email.com"));

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        then(passwordResetTokenGateway).should().save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getUserId()).isEqualTo(userId);
        assertThat(savedToken.isUsed()).isFalse();
        then(passwordResetTokenGateway).should().markAllAsUsedByUserId(eq(userId), any(OffsetDateTime.class));

        then(accountNotificationGateway).should().sendPasswordResetEmail(
                eq(user),
                codeCaptor.capture(),
                eq(savedToken.getExpiresAt())
        );
        assertThat(codeCaptor.getValue()).matches("\\d{6}");
        assertThat(savedToken.getTokenHash()).isEqualTo("hash-" + codeCaptor.getValue());
    }

    @Test
    void shouldIgnoreForgotPasswordWhenEmailDoesNotExist() {
        when(userGateway.findByEmail(anyString())).thenReturn(Optional.empty());

        forgotPasswordUseCase.execute(new ForgotPasswordCommand("desconhecido@email.com"));

        then(refreshTokenHasher).shouldHaveNoInteractions();
        then(passwordResetTokenGateway).shouldHaveNoInteractions();
        then(accountNotificationGateway).shouldHaveNoInteractions();
    }
}
