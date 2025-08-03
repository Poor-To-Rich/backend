package com.poortorich.chat.realtime.payload;

import com.poortorich.chat.entity.enums.MessageType;
import java.time.LocalDateTime;

public class ChatroomClosedResponseMessage {

    private Long messageId;
    private Long chatroomId;
    private MessageType messageType;
    private String content;
    private LocalDateTime sendAt;
}
