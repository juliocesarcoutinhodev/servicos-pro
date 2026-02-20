package com.servicepro.auth.domain.gateway;

import com.servicepro.auth.domain.model.User;
import java.time.OffsetDateTime;

public interface AccountNotificationGateway {

    void sendWelcomeEmail(User user);

    void sendPasswordResetEmail(User user, String otpCode, OffsetDateTime expiresAt);
}
