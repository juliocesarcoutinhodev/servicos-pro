package com.servicepro.auth.application.usecase.forgotpassword;

import com.servicepro.auth.domain.gateway.AccountNotificationGateway;
import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.PasswordResetToken;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.domain.model.valueobject.Email;
import com.servicepro.auth.infrastructure.config.AuthLinksProperties;
import com.servicepro.auth.infrastructure.config.AuthPasswordResetProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCaseImpl implements ForgotPasswordUseCase {

    private final UserGateway userGateway;
    private final TokenGateway tokenGateway;
    private final RefreshTokenHasher refreshTokenHasher;
    private final PasswordResetTokenGateway passwordResetTokenGateway;
    private final AuthPasswordResetProperties authPasswordResetProperties;
    private final AuthLinksProperties authLinksProperties;
    private final AccountNotificationGateway accountNotificationGateway;

    @Override
    @Transactional
    public void execute(ForgotPasswordCommand command) {
        Email email = Email.of(command.email());
        User user = userGateway.findByEmail(email.value()).orElse(null);
        if (user == null) {
            return;
        }

        String rawToken = tokenGateway.generateRefreshToken();
        String tokenHash = refreshTokenHasher.hash(rawToken);
        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC)
                .plusSeconds(authPasswordResetProperties.getTokenTtlSeconds());

        PasswordResetToken token = PasswordResetToken.issue(user.getId(), tokenHash, expiresAt);
        passwordResetTokenGateway.save(token);

        String resetLink = buildResetLink(rawToken);
        sendResetEmailAfterCommit(user, resetLink, expiresAt);
    }

    private String buildResetLink(String rawToken) {
        String encodedToken = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String baseUrl = authLinksProperties.getResetPasswordUrl();

        if (baseUrl.contains("%s")) {
            return baseUrl.formatted(encodedToken);
        }

        String separator = baseUrl.contains("?") ? "&" : "?";
        return baseUrl + separator + "token=" + encodedToken;
    }

    private void sendResetEmailAfterCommit(User user, String resetLink, OffsetDateTime expiresAt) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            accountNotificationGateway.sendPasswordResetEmail(user, resetLink, expiresAt);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                accountNotificationGateway.sendPasswordResetEmail(user, resetLink, expiresAt);
            }
        });
    }
}
