package com.servicepro.auth.application.service.ratelimit;

import com.servicepro.auth.domain.exception.RateLimitExceededException;
import com.servicepro.auth.domain.gateway.RateLimitGateway;
import com.servicepro.auth.infrastructure.config.AuthRateLimitProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthRateLimitServiceImpl implements AuthRateLimitService {

    private static final String KEY_PATTERN = "rate_limit:%s:%s";

    private final RateLimitGateway rateLimitGateway;
    private final AuthRateLimitProperties properties;

    @Override
    public RateLimitStatus consume(AuthRateLimitAction action, String clientIp) {
        Duration ttl = Duration.ofSeconds(properties.getWindowSeconds());
        String key = KEY_PATTERN.formatted(action.keySegment(), sanitizeIp(clientIp));

        long currentCount = rateLimitGateway.incrementAndGet(key, ttl);
        long resetInSeconds = normalizeReset(rateLimitGateway.ttlSeconds(key));
        long remaining = Math.max(0, (long) properties.getMaxRequestsPerWindow() - currentCount);

        if (currentCount > properties.getMaxRequestsPerWindow()) {
            throw new RateLimitExceededException(
                    action.keySegment(),
                    properties.getMaxRequestsPerWindow(),
                    remaining,
                    resetInSeconds,
                    properties.getWindowSeconds()
            );
        }

        return new RateLimitStatus(properties.getMaxRequestsPerWindow(), remaining, resetInSeconds);
    }

    private long normalizeReset(long ttlSeconds) {
        if (ttlSeconds <= 0) {
            return properties.getWindowSeconds();
        }
        return ttlSeconds;
    }

    private String sanitizeIp(String clientIp) {
        if (clientIp == null || clientIp.isBlank()) {
            return "unknown";
        }
        return clientIp.trim();
    }
}
