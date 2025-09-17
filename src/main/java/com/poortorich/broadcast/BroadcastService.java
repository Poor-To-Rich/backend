package com.poortorich.broadcast;

import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastInChatroom(Long chatroomId, Object... objects) {
        for (Object obj : objects) {
            broadcastInChatroom(chatroomId, obj);
        }
    }

    public void broadcastInChatroom(Long chatroomId, Object object) {
        if (Objects.nonNull(object)) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                    object);
        }
    }

    public void broadcastInMyChatroom(Long userId, Object... objects) {
        for (Object obj : objects) {
            broadcastInMyChatroom(userId, obj);
        }
    }

    public void broadcastInMyChatroom(Long userId, Object obejct) {
        if (Objects.nonNull(obejct)) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.JOINED_CHATROOM_LIST_PREFIX + userId,
                    obejct);
        }
    }
}
