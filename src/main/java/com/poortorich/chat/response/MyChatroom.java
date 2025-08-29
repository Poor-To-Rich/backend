package com.poortorich.chat.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyChatroom implements Comparable<MyChatroom> {

    private final Long chatroomId;
    private final String chatroomImage;
    private final Boolean isHost;
    private final String chatroomTitle;
    private final Long currentMemberCount;
    private final String lastMessage;
    private final LocalDateTime lastMessageTime;
    private final Long unreadMessageCount;

    @Override
    public int compareTo(MyChatroom other) {
        return other.getLastMessageTime().compareTo(this.lastMessageTime);
    }
}
