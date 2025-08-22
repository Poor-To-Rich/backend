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

    private String getSubscriptionKey(String sessionId, String subscriptionId) {
        return sessionId + subscriptionId;
    }

    public void subscribe(Long chatroomId, String username, String sessionId, String subscriptionId) {
        try {
            setOps.add(getChatroomKey(chatroomId), username);

            valueOps.set(getSubscriptionKey(sessionId, subscriptionId), chatroomId.toString());
        } catch (DataAccessException exception) {
            throw new InternalServerErrorException(StompResponse.SUBSCRIBER_SAVE_FAILURE);
        }
    }

    public void unsubscribe(String username, String sessionId, String subscriptionId) {
        try {
            String subscriptionKey = getSubscriptionKey(sessionId, subscriptionId);
            String chatroomId = valueOps.get(subscriptionKey);

            if (!Objects.isNull(chatroomId)) {
                setOps.remove(getChatroomKey(Long.valueOf(chatroomId)), username);
                redisTemplate.delete(subscriptionKey);
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

    public void unsubscribeAll(String username, String sessionId) {
        try {
            Set<String> keys = redisTemplate.keys(sessionId + "*");

            for (String key : keys) {
                String chatroomId = valueOps.get(key);
                if (!Objects.isNull(chatroomId)) {
                    setOps.remove(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId, username);
                    redisTemplate.delete(key);
                }
            }
        } catch (DataAccessException exception) {
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }
}
