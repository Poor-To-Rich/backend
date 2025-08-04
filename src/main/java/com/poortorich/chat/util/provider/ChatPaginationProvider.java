package com.poortorich.chat.util;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.model.ChatPaginationContext;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatroomService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatPaginationProvider {

    private final ChatroomService chatroomService;
    private final ChatMessageService chatMessageService;

    public ChatPaginationContext getContext(Long chatroomId, Long cursor, Long pageSize) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        return ChatPaginationContext.builder()
                .chatroom(chatroom)
                .cursor(getCursor(chatroom, cursor))
                .pageRequest(getPageRequest(pageSize))
                .build();
    }

    private Long getCursor(Chatroom chatroom, Long cursor) {
        if (!Objects.isNull(cursor)) {
            return cursor;
        }

        return chatMessageService.getLatestMessageId(chatroom);
    }

    private PageRequest getPageRequest(Long pageSize) {
        return PageRequest.of(0, pageSize.intValue());
    }
}
