package com.poortorich.chat.realtime.payload;

import com.poortorich.chat.entity.enums.MessageType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLeaveResponsePayload {

    private Long userId;
    private Long messageId;
    private Long chatroomId;
    private MessageType messageType;
    private String content;
    private LocalDateTime sendAt;
}
