package com.servicepro.auth.domain.gateway;

import com.servicepro.auth.domain.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenGateway {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
