package com.servicepro.auth.application.usecase.forgotpassword;

import com.servicepro.auth.domain.gateway.AccountNotificationGateway;
import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.PasswordResetToken;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.domain.model.valueobject.Email;
import com.servicepro.auth.infrastructure.config.AuthPasswordResetProperties;
import java.security.SecureRandom;
import java.util.Locale;
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

    private static final int OTP_LENGTH = 6;
    private static final int OTP_BOUND = 1_000_000;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserGateway userGateway;
    private final RefreshTokenHasher refreshTokenHasher;
    private final PasswordResetTokenGateway passwordResetTokenGateway;
    private final AuthPasswordResetProperties authPasswordResetProperties;
    private final AccountNotificationGateway accountNotificationGateway;

    @Override
    @Transactional
    public void execute(ForgotPasswordCommand command) {
        Email email = Email.of(command.email());
        User user = userGateway.findByEmail(email.value()).orElse(null);
        if (user == null) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        passwordResetTokenGateway.markAllAsUsedByUserId(user.getId(), now);

        String otpCode = generateOtpCode();
        String tokenHash = refreshTokenHasher.hash(otpCode);
        OffsetDateTime expiresAt = now
                .plusSeconds(authPasswordResetProperties.getTokenTtlSeconds());

        PasswordResetToken token = PasswordResetToken.issue(user.getId(), tokenHash, expiresAt);
        passwordResetTokenGateway.save(token);

        sendResetEmailAfterCommit(user, otpCode, expiresAt);
    }

    private String generateOtpCode() {
        int rawCode = SECURE_RANDOM.nextInt(OTP_BOUND);
        return String.format(Locale.ROOT, "%0" + OTP_LENGTH + "d", rawCode);
    }

    private void sendResetEmailAfterCommit(User user, String otpCode, OffsetDateTime expiresAt) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            accountNotificationGateway.sendPasswordResetEmail(user, otpCode, expiresAt);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                accountNotificationGateway.sendPasswordResetEmail(user, otpCode, expiresAt);
            }
        });
    }
}
