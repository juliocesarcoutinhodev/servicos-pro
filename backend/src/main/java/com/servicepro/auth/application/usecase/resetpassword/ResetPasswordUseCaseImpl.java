package com.servicepro.auth.application.usecase.resetpassword;

import com.servicepro.auth.domain.exception.InvalidPasswordResetTokenException;
import com.servicepro.auth.domain.exception.InvalidSignupPasswordException;
import com.servicepro.auth.domain.gateway.PasswordHasher;
import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenHasher;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.PasswordResetToken;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 72;

    private final PasswordResetTokenGateway passwordResetTokenGateway;
    private final RefreshTokenHasher refreshTokenHasher;
    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenGateway refreshTokenGateway;
    private final RefreshTokenCacheGateway refreshTokenCacheGateway;

    @Override
    @Transactional
    public void execute(ResetPasswordCommand command) {
        validatePassword(command.newPassword());

        String rawToken = command.token();
        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidPasswordResetTokenException();
        }

        String tokenHash = refreshTokenHasher.hash(rawToken);
        PasswordResetToken passwordResetToken = passwordResetTokenGateway.findByTokenHash(tokenHash)
                .orElseThrow(InvalidPasswordResetTokenException::new);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (passwordResetToken.isUsed() || passwordResetToken.isExpired(now)) {
            throw new InvalidPasswordResetTokenException();
        }

        userGateway.findById(passwordResetToken.getUserId()).orElseThrow(InvalidPasswordResetTokenException::new);

        String newPasswordHash = passwordHasher.hash(command.newPassword());
        userGateway.updatePasswordHash(passwordResetToken.getUserId(), newPasswordHash);

        refreshTokenGateway.revokeAllByUserId(passwordResetToken.getUserId());
        refreshTokenCacheGateway.evictAll(passwordResetToken.getUserId());
        passwordResetTokenGateway.save(passwordResetToken.markAsUsed(now));
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidSignupPasswordException();
        }
        if (rawPassword.length() < PASSWORD_MIN_LENGTH || rawPassword.length() > PASSWORD_MAX_LENGTH) {
            throw new InvalidSignupPasswordException();
        }
    }
}
