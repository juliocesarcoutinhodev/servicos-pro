package com.servicepro.auth.infrastructure.cache;

import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenRedisAdapter implements RefreshTokenCacheGateway {

    private static final String KEY_PATTERN = "refresh_token:%s:%s";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void put(UUID userId, String jti, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(buildKey(userId, jti), value, ttl);
    }

    @Override
    public void evict(UUID userId, String jti) {
        stringRedisTemplate.delete(buildKey(userId, jti));
    }

    private String buildKey(UUID userId, String jti) {
        return KEY_PATTERN.formatted(userId, jti);
    }
}
