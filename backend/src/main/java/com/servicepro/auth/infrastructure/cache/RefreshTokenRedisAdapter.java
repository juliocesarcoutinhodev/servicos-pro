package com.servicepro.auth.infrastructure.cache;

import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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
    public Optional<String> get(UUID userId, String jti) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(buildKey(userId, jti)));
    }

    @Override
    public void evict(UUID userId, String jti) {
        stringRedisTemplate.delete(buildKey(userId, jti));
    }

    @Override
    public void evictAll(UUID userId) {
        Set<String> keys = scanKeysByUserId(userId);
        if (!keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    private Set<String> scanKeysByUserId(UUID userId) {
        String pattern = KEY_PATTERN.formatted(userId, "*");
        Set<String> keys = stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> scanKeys(connection, pattern));
        return keys == null ? Set.of() : keys;
    }

    private Set<String> scanKeys(RedisConnection connection, String pattern) {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(500)
                .build();

        try (Cursor<byte[]> cursor = connection.scan(options)) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }
        }
        return keys;
    }

    private String buildKey(UUID userId, String jti) {
        return KEY_PATTERN.formatted(userId, jti);
    }
}
