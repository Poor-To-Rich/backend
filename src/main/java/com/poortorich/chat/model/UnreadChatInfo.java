package com.poortorich.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UnreadChatInfo {

    private final Long chatroomId;
    private final Long userId;
    private final Long lastReadMessageId;
}
