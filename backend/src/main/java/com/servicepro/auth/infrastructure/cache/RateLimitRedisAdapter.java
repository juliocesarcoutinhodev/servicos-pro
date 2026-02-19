package com.servicepro.auth.infrastructure.cache;

import com.servicepro.auth.domain.gateway.RateLimitGateway;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateLimitRedisAdapter implements RateLimitGateway {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public long incrementAndGet(String key, Duration ttl) {
        Long current = stringRedisTemplate.opsForValue().increment(key);
        if (current == null) {
            throw new IllegalStateException("Nao foi possivel incrementar o contador de rate limit.");
        }

        if (current == 1L) {
            stringRedisTemplate.expire(key, ttl);
            return current;
        }

        Long currentTtl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (currentTtl == null || currentTtl <= 0) {
            stringRedisTemplate.expire(key, ttl);
        }
        return current;
    }

    @Override
    public long ttlSeconds(String key) {
        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl == null ? 0 : ttl;
    }
}
