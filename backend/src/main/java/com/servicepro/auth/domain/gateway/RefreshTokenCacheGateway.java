package com.servicepro.auth.domain.gateway;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenCacheGateway {

    void put(UUID userId, String jti, String value, Duration ttl);

    Optional<String> get(UUID userId, String jti);

    void evict(UUID userId, String jti);

    void evictAll(UUID userId);
}
