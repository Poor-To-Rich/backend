package com.poortorich.chat.util.mapper;

import com.poortorich.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatMessageContentMapper {

    private static final String RANKING_MESSAGE = "랭킹이 집계되었습니다.";
    private static final String PHOTO_MESSAGE = "사진을 보냈습니다.";

    public String mapToContent(ChatMessage chatMessage) {
        if (Objects.isNull(chatMessage) || Objects.isNull(chatMessage.getMessageType())) {
            return null;
        }
        
        return switch (chatMessage.getMessageType()) {
            case PHOTO -> PHOTO_MESSAGE;
            case TEXT -> chatMessage.getContent();
            case RANKING -> RANKING_MESSAGE;
            default -> null;
        };
    }
}
