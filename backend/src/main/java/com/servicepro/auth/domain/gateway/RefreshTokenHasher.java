package com.servicepro.auth.domain.gateway;

public interface RefreshTokenHasher {

    String hash(String rawToken);
}
