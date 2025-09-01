package com.poortorich.chat.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnreadChatInfo {

    private final Long chatroomId;
    private final Long userId;
    private final Long lastReadMessageId;
}
