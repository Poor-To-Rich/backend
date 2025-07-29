package com.poortorich.chat.realtime.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.realtime.payload.ResponsePayload;
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

    public ResponsePayload createUserEnterSystemMessage(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        UserEnterResponsePayload payload = chatMessageService.saveUserEnterMessage(user, chatroom);

        return ResponsePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(payload)
                .build();
    }
}
