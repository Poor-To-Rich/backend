package com.poortorich.chat.repository;

import com.poortorich.chat.request.enums.SortBy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisChatRepository {

    private static final String REDIS_CHAT_KEY = "chatrooms:list:%s";
    private static final int CHAT_KEY_EXPIRATION_TIME = 5;

    private final RedisTemplate<String, String> redisTemplate;

    public void save(SortBy sortBy, List<Long> chatroomIds) {
        String key = getRedisKey(sortBy.name());
        List<String> stringIds = chatroomIds.stream()
                .map(String::valueOf)
                .toList();

        redisTemplate.opsForList().rightPushAll(key, stringIds);
        redisTemplate.expire(key, Duration.ofMinutes(CHAT_KEY_EXPIRATION_TIME));
    }

    public List<Long> getChatroomIds(SortBy sortBy, Long cursor, int size) {
        String key = getRedisKey(sortBy.name());
        List<String> allIds = redisTemplate.opsForList().range(key, 0, -1);
        if (allIds == null || allIds.isEmpty()) {
            return List.of();
        }

        if (cursor == 0 || cursor == -1) {
            return allIds.subList(0, Math.min(size, allIds.size())).stream()
                    .map(Long::parseLong)
                    .toList();
        }

        int startIndex = allIds.indexOf(cursor.toString());
        List<String> result = allIds.subList(startIndex, Math.min(startIndex + size, allIds.size()));
        return result.stream()
                .map(Long::parseLong)
                .toList();
    }

    public boolean existsBySortBy(SortBy sortBy) {
        String key = getRedisKey(sortBy.name());
        return redisTemplate.hasKey(key);
    }

    public Boolean hasNext(SortBy sortBy, Long lastChatroomId) {
        List<String> stringIds = getAllList(sortBy);
        if (stringIds == null || stringIds.isEmpty()) {
            return false;
        }

        int idx = stringIds.indexOf(lastChatroomId.toString());
        return hasNext(idx, stringIds.size());
    }

    public Long getNextCursor(SortBy sortBy, Long lastChatroomId) {
        List<String> stringIds = getAllList(sortBy);
        if (stringIds == null || stringIds.isEmpty()) {
            return null;
        }

        int idx = stringIds.indexOf(lastChatroomId.toString());
        if (hasNext(idx, stringIds.size())) {
            return Long.parseLong(stringIds.get(idx + 1));
        }

        return null;
    }

    private List<String> getAllList(SortBy sortBy) {
        String key = getRedisKey(sortBy.name());
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    private Boolean hasNext(int idx, int size) {
        return idx != -1 && idx + 1 < size;
    }

    private String getRedisKey(String sortBy) {
        return String.format(REDIS_CHAT_KEY, sortBy);
    }
}
