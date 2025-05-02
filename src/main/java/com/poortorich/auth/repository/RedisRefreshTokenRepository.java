package com.poortorich.auth.repository;

import com.poortorich.auth.jwt.constants.JwtConstants;
import com.poortorich.auth.repository.interfaces.RefreshTokenRepository;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(Long userId, String token) {
        String key = getRedisKey(userId, token);
        redisTemplate.opsForValue().set(
                key,
                Boolean.TRUE.toString(),
                JwtConstants.REFRESH_TOKEN_COOKIE_EXPIRY,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public boolean existsByUserIdAndToken(Long userId, String token) {
        String key = getRedisKey(userId, token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void deleteByUserIdAndToken(Long userId, String token) {
        String key = getRedisKey(userId, token);
        redisTemplate.delete(key);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        Set<String> keys = redisTemplate.keys(getRedisKey(userId));
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private String getRedisKey(Long userId, String token) {
        return String.format(JwtConstants.REDIS_REFRESH_TOKEN_KEY, userId, token);
    }

    private String getRedisKey(Long userId) {
        return String.format(JwtConstants.REDIS_REFRESH_TOKEN_SCAN_KEY, userId);
    }
}
