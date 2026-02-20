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
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.domain.model.valueobject.Email;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 72;
    private static final int OTP_LENGTH = 6;
    private static final Pattern OTP_PATTERN = Pattern.compile("^\\d{" + OTP_LENGTH + "}$");

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
        validateOtpCode(command.code());

        Email email = Email.of(command.email());
        User user = userGateway.findByEmail(email.value()).orElseThrow(InvalidPasswordResetTokenException::new);

        String tokenHash = refreshTokenHasher.hash(command.code());
        PasswordResetToken passwordResetToken = passwordResetTokenGateway
                .findLatestActiveByUserIdAndTokenHash(user.getId(), tokenHash)
                .orElseThrow(InvalidPasswordResetTokenException::new);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (passwordResetToken.isUsed() || passwordResetToken.isExpired(now)) {
            throw new InvalidPasswordResetTokenException();
        }

        String newPasswordHash = passwordHasher.hash(command.newPassword());
        userGateway.updatePasswordHash(user.getId(), newPasswordHash);

        refreshTokenGateway.revokeAllByUserId(user.getId());
        refreshTokenCacheGateway.evictAll(user.getId());
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

    private void validateOtpCode(String otpCode) {
        if (otpCode == null || !OTP_PATTERN.matcher(otpCode).matches()) {
            throw new InvalidPasswordResetTokenException();
        }
    }
}
