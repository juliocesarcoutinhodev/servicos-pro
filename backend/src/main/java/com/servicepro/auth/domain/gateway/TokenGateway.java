package com.servicepro.auth.domain.gateway;

import com.servicepro.auth.domain.model.AccessTokenClaims;
import com.servicepro.auth.domain.model.User;

public interface TokenGateway {

    String generateAccessToken(User user);

    String generateRefreshToken();

    boolean validateToken(String token);

    AccessTokenClaims extractClaims(String token);

    long accessTokenTtlSeconds();

    long refreshTokenTtlSeconds();
}
