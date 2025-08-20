package com.poortorich.chat.model;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;

@Builder
public record ChatPaginationContext(
        Chatroom chatroom,
        ChatParticipant chatParticipant,
        Long cursor,
        PageRequest pageRequest
) {
}
