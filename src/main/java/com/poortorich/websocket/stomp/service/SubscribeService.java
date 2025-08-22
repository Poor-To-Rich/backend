package com.poortorich.websocket.stomp.service;

import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.response.enums.GlobalResponse;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import com.poortorich.websocket.stomp.response.StompResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final StringRedisTemplate redisTemplate;
    private SetOperations<String, String> setOps;
    private ValueOperations<String, String> valueOps;

    @PostConstruct
    public void init() {
        setOps = redisTemplate.opsForSet();
        valueOps = redisTemplate.opsForValue();
    }

    private String getChatroomKey(Long chatroomId) {
        return SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId;
    }

    private String getChatroomKey(String chatroomId) {
        return SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId;
    }

    private String getSubscriptionKey(String sessionId, String subscriptionId) {
        return sessionId + subscriptionId;
    }

    private String getUsernameSessionKey(String username, String sessionId) {
        return username + sessionId;
    }

    public void subscribe(Long chatroomId, String username, String sessionId, String subscriptionId) {
        try {
            setOps.add(getChatroomKey(chatroomId), username);
            valueOps.set(getSubscriptionKey(sessionId, subscriptionId), chatroomId.toString());
            setOps.add(getUsernameSessionKey(username, sessionId), getSubscriptionKey(sessionId, subscriptionId));
        } catch (DataAccessException exception) {
            throw new InternalServerErrorException(StompResponse.SUBSCRIBER_SAVE_FAILURE);
        }
    }

    public void unsubscribe(String username, String sessionId, String subscriptionId) {
        try {
            String subscriptionKey = getSubscriptionKey(sessionId, subscriptionId);
            String chatroomId = valueOps.get(subscriptionKey);

            if (!Objects.isNull(chatroomId)) {
                setOps.remove(getChatroomKey(chatroomId), username);
                redisTemplate.delete(subscriptionKey);
                setOps.remove(
                        getUsernameSessionKey(username, sessionId),
                        getSubscriptionKey(sessionId, subscriptionId));
            }
        } catch (DataAccessException exception) {
            throw new InternalServerErrorException(StompResponse.SUBSCRIBER_REMOVE_FAILURE);
        }
    }

    public Set<String> getSubscribers(Long chatroomId) {
        try {
            return setOps.members(getChatroomKey(chatroomId));
        } catch (DataAccessException exception) {
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }

    public void cleanupSession(String username, String sessionId) {
        try {
            String userSessionKey = getUsernameSessionKey(username, sessionId);
            Set<String> keys = setOps.members(userSessionKey);

            if (Objects.isNull(keys)) {
                return;
            }

            for (String key : keys) {
                String chatroomId = valueOps.get(key);
                if (!Objects.isNull(chatroomId)) {
                    setOps.remove(getChatroomKey(chatroomId), username);
                    redisTemplate.delete(key);
                }
            }

            redisTemplate.delete(userSessionKey);
        } catch (DataAccessException exception) {
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }
}
