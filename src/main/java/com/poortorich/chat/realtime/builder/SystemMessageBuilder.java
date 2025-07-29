package com.poortorich.chat.realtime.builder;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.user.entity.User;

public class SystemMessageBuilder {

    private static final String ENTER_CONTENT_SUFFIX = "님이 입장했습니다.";

    public static ChatMessage buildEnterMessage(User user, Chatroom chatroom) {
        return ChatMessage.builder()
                .messageType(MessageType.ENTER)
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .content(user.getNickname() + ENTER_CONTENT_SUFFIX)
                .build();
    }
}
