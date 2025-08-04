package com.poortorich.chat.realtime.payload;

import com.poortorich.chat.entity.enums.MessageType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserChatMessagePayload {

    private Long messageId;
    private Long chatroomId;
    private Long senderId;
    private MessageType messageType;
    private String content;
    private LocalDateTime sendAt;
    private List<Long> unreadBy;
}
