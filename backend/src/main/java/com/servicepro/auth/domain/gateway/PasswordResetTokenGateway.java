package com.servicepro.auth.domain.gateway;

import com.servicepro.auth.domain.model.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenGateway {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
