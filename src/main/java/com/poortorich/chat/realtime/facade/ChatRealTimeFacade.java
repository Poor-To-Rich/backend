package com.poortorich.chat.realtime.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.manager.ChatroomLeaveManager;
import com.poortorich.chat.realtime.collect.ChatPayloadCollector;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.BasePayload;
import com.poortorich.chat.realtime.payload.UserEnterResponsePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRealTimeFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatMessageService chatMessageService;

    private final ChatPayloadCollector payloadCollector;
    private final ChatroomLeaveManager chatroomLeaveManager;

    public BasePayload createUserEnterSystemMessage(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        if (chatroom.getIsDeleted()) {
            return null;
        }

        UserEnterResponsePayload payload = chatMessageService.saveUserEnterMessage(user, chatroom);

        return BasePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(payload)
                .build();
    }

    public BasePayload createUserLeaveSystemMessage(String username, Long chatroomId) {
        PayloadContext context = payloadCollector.getPayloadContext(username, chatroomId);

        return chatroomLeaveManager.leaveChatroom(context);
    }
}
