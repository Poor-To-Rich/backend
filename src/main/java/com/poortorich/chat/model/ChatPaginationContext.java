package com.poortorich.chat.model;

import com.poortorich.chat.entity.ChatParticipant;
import lombok.Builder;

@Builder
public record PaginationContext(
        ChatParticipant chatParticipant,
        Long cursor,
        Long pageSize
) {
}
