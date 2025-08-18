package com.poortorich.websocket.stomp.service;

import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.response.enums.GlobalResponse;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import com.poortorich.websocket.stomp.response.StompResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final StringRedisTemplate redisTemplate;
    private SetOperations<String, String> setOps;

    @PostConstruct
    public void init() {
        setOps = redisTemplate.opsForSet();
    }

    private String getKey(Long chatroomId) {
        return SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId;
    }

    public void subscribe(Long chatroomId, String username) {
        try {
            setOps.add(getKey(chatroomId), username);
        } catch (RedisConnectionFailureException exception) {
            throw new InternalServerErrorException(StompResponse.SUBSCRIBER_SAVE_FAILURE);
        }
    }

    public void unsubscribe(Long chatroomId, String username) {
        try {
            setOps.remove(getKey(chatroomId), username);
        } catch (RedisConnectionFailureException exception) {
            throw new InternalServerErrorException(StompResponse.SUBSCRIBER_REMOVE_FAILURE);
        }
    }

    public Set<String> getSubscribers(Long chatroomId) {
        try {
            return setOps.members(getKey(chatroomId));
        } catch (RedisConnectionFailureException exception) {
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }
}
