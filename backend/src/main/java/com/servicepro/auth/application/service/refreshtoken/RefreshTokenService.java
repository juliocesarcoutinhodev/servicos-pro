package com.servicepro.auth.application.service.refreshtoken;

import java.util.UUID;

public interface RefreshTokenService {

    IssuedRefreshToken create(UUID userId);

    void revoke(String rawRefreshToken);
}
