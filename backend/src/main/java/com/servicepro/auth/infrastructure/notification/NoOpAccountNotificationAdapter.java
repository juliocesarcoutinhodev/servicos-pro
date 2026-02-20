package com.servicepro.auth.infrastructure.notification;

import com.servicepro.auth.domain.gateway.AccountNotificationGateway;
import com.servicepro.auth.domain.model.User;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpAccountNotificationAdapter implements AccountNotificationGateway {

    @Override
    public void sendWelcomeEmail(User user) {
        log.debug("Email de boas-vindas ignorado (mail desabilitado). userId={}", user.getId());
    }

    @Override
    public void sendPasswordResetEmail(User user, String otpCode, OffsetDateTime expiresAt) {
        log.debug("Email de reset ignorado (mail desabilitado). userId={}, expiresAt={}", user.getId(), expiresAt);
    }
}
