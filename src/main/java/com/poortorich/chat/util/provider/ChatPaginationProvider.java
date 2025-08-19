package com.poortorich.chat.util.provider;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.model.ChatPaginationContext;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatPaginationProvider {

    private final ChatroomService chatroomService;
    private final ChatMessageService chatMessageService;
    private final ChatParticipantService chatParticipantService;
    private final UserService userService;

    public ChatPaginationContext getContext(String username, Long chatroomId, Long cursor, Long pageSize) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);
        return ChatPaginationContext.builder()
                .chatroom(chatroom)
                .chatParticipant(chatParticipant)
                .cursor(getCursor(chatroom, cursor))
                .pageRequest(getPageRequest(pageSize))
                .build();
    }

    public Long getNextCursor(Slice<ChatMessage> chatMessages) {
        if (chatMessages.hasContent()) {
            return chatMessages.getContent().getLast().getId() - 1;
        }
        return null;
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
