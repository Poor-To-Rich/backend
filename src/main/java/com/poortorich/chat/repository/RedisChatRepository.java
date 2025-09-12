package com.poortorich.chat.repository;

import com.poortorich.chat.request.enums.SortBy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisChatRepository {

    private static final String REDIS_CHAT_ZSET_KEY = "chatrooms:zset:%s";
    private static final int CHAT_KEY_EXPIRATION_TIME = 5;

    private final RedisTemplate<String, String> redisTemplate;

    public void overwrite(SortBy sortBy, List<Long> chatroomIds, List<String> lastMessageTimes) {
        String key = getRedisKey(sortBy.name());
        redisTemplate.delete(key);

        save(sortBy, chatroomIds, lastMessageTimes);
    }

    public void save(SortBy sortBy, List<Long> chatroomIds, List<String> lastMessageTimes) {
        String key = getRedisKey(sortBy.name());
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();

        for (int i = 0; i < chatroomIds.size(); i++) {
            String member = String.valueOf(chatroomIds.get(i));
            double score = toEpochMillis(lastMessageTimes.get(i));
            zset.add(key, member, score);
        }

        redisTemplate.expire(key, Duration.ofMinutes(CHAT_KEY_EXPIRATION_TIME));
    }

    public List<Long> getChatroomIds(SortBy sortBy, Long cursor, int size) {
        var tuples = getWindowTuples(sortBy, cursor, size);
        if (tuples.isEmpty()) {
            return List.of();
        }

        return tuples.stream()
                .map(t -> Long.parseLong(Objects.requireNonNull(t.getValue())))
                .toList();
    }

    public List<String> getLastMessageTimes(SortBy sortBy, Long cursor, int size) {
        var tuples = getWindowTuples(sortBy, cursor, size);
        if (tuples.isEmpty()) {
            return List.of();
        }

        return tuples.stream()
                .map(t -> {
                    Double s = t.getScore();
                    if (s == null || s.doubleValue() == 0D) return null; // 메시지 없음은 null
                    return Instant.ofEpochMilli(s.longValue())
                            .atZone(ZoneId.of("Asia/Seoul"))
                            .toLocalDateTime()
                            .toString();
                })
                .toList();
    }

    private Set<ZSetOperations.TypedTuple<String>> getWindowTuples(SortBy sortBy, Long cursor, int size) {
        String key = getRedisKey(sortBy.name());
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();

        Long total = zset.size(key);
        if (total == null || total == 0) {
            return Set.of();
        }

        long startIndex;
        if (cursor == null || cursor == 0 || cursor == -1) {
            startIndex = 0;
        } else {
            Long rank = zset.reverseRank(key, String.valueOf(cursor));
            if (rank == null) return Set.of();
            startIndex = rank + 1;
        }
        long endIndex = Math.min(startIndex + size - 1, total - 1);
        if (startIndex > endIndex) return Set.of();

        Set<ZSetOperations.TypedTuple<String>> tuples = zset.reverseRangeWithScores(key, startIndex, endIndex);
        return (tuples == null) ? Set.of() : tuples;
    }

    public boolean existsBySortBy(SortBy sortBy) {
        String key = getRedisKey(sortBy.name());
        return redisTemplate.hasKey(key);
    }

    public Boolean hasNext(SortBy sortBy, Long lastChatroomId) {
        String zsetKey = getRedisKey(sortBy.name());
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();

        Long total = zset.size(zsetKey);
        if (total == null || total == 0) {
            return false;
        }

        Long rank = zset.reverseRank(zsetKey, String.valueOf(lastChatroomId));
        return rank != null && (rank + 1) < total;
    }

    public Long getNextCursor(SortBy sortBy, Long lastChatroomId) {
        String zsetKey = getRedisKey(sortBy.name());
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();

        Long total = zset.size(zsetKey);
        if (total == null || total == 0) {
            return null;
        }
        Long rank = zset.reverseRank(zsetKey, String.valueOf(lastChatroomId));
        if (rank == null) {
            return null;
        }
        long nextIndex = rank + 1;
        if (nextIndex >= total) {
            return null;
        }
        Set<String> range = zset.reverseRange(zsetKey, nextIndex, nextIndex);
        if (range == null || range.isEmpty()) {
            return null;
        }

        return Long.parseLong(range.iterator().next());
    }

    private String getRedisKey(String sortBy) {
        return String.format(REDIS_CHAT_ZSET_KEY, sortBy);
    }

    private double toEpochMillis(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isBlank()) {
            return 0D;
        }

        try {
            return (double) Instant.parse(isoDateTime).toEpochMilli();
        } catch (DateTimeParseException ignored) { }
        try {
            return (double) java.time.LocalDateTime.parse(isoDateTime)
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toInstant()
                    .toEpochMilli();
        } catch (DateTimeParseException ignored) { }
        try {
            return Double.parseDouble(isoDateTime);
        } catch (NumberFormatException ignored) {
            return 0D;
        }
    }
}
