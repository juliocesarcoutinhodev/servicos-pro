package com.servicepro.auth.domain.gateway;

import java.time.Duration;

public interface RateLimitGateway {

    long incrementAndGet(String key, Duration ttl);

    long ttlSeconds(String key);
}
