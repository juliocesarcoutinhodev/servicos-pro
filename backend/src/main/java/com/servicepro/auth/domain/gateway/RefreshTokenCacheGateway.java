package com.servicepro.auth.domain.gateway;

import java.time.Duration;
import java.util.UUID;

public interface RefreshTokenCacheGateway {

    void put(UUID userId, String jti, String value, Duration ttl);

    void evict(UUID userId, String jti);
}
