package com.poortorich.user.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUserReservationService {

    private static final String USERNAME_KEY_PREFIX = "username_reservation:";
    private static final String NICKNAME_KEY_PREFIX = "nickname_reservation:";
    private static final long RESERVATION_TTL = 10L;

    private final RedisTemplate<String, String> redisTemplate;

    public void reservedUsername(String username) {
        save(USERNAME_KEY_PREFIX + username);
    }

    public void reservedNickname(String nickname) {
        save(NICKNAME_KEY_PREFIX + nickname);
    }

    public boolean existsByUsername(String username) {
        return existsByKey(USERNAME_KEY_PREFIX + username);
    }

    public boolean existsByNickname(String nickname) {
        return existsByKey(NICKNAME_KEY_PREFIX + nickname);
    }

    public void removeUsernameReservation(String username) {
        delete(USERNAME_KEY_PREFIX + username);
    }

    public void removeNicknameReservation(String nickname) {
        delete(NICKNAME_KEY_PREFIX + nickname);
    }

    private void save(String redisKey) {
        redisTemplate.opsForValue().set(
                redisKey,
                Boolean.TRUE.toString(),
                RESERVATION_TTL,
                TimeUnit.MINUTES
        );
    }

    private boolean existsByKey(String redisKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    private void delete(String redisKey) {
        redisTemplate.delete(redisKey);
    }
}
