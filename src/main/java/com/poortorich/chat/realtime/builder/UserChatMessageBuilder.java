package com.poortorich.chat.realtime.builder;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.photo.repository.PhotoRepository;
import com.poortorich.photo.response.enums.PhotoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserChatMessageBuilder {

    private final PhotoRepository photoRepository;

    public ChatMessage buildChatMessage(
            ChatParticipant chatParticipant,
            ChatMessageRequestPayload chatMessageRequestPayload
    ) {
        return switch (chatMessageRequestPayload.getMessageType()) {
            case TEXT -> buildTextChatMessage(chatParticipant, chatMessageRequestPayload);
            case PHOTO -> buildPhotoChatMessage(chatParticipant, chatMessageRequestPayload);
            default -> throw new BadRequestException(ChatResponse.MESSAGE_TYPE_INVALID);
        };
    }

    private ChatMessage buildTextChatMessage(
            ChatParticipant chatParticipant,
            ChatMessageRequestPayload chatMessageRequestPayload
    ) {
        return ChatMessage.builder()
                .userId(chatParticipant.getUser().getId())
                .type(ChatMessageType.CHAT_MESSAGE)
                .messageType(chatMessageRequestPayload.getMessageType())
                .content(chatMessageRequestPayload.getContent())
                .chatroom(chatParticipant.getChatroom())
                .build();
    }

    private ChatMessage buildPhotoChatMessage(
            ChatParticipant chatParticipant,
            ChatMessageRequestPayload chatMessageRequestPayload
    ) {
        Long photoId = photoRepository.findByPhotoUrl(chatMessageRequestPayload.getContent())
                .orElseThrow(() -> new NotFoundException(PhotoResponse.PHOTO_NOT_FOUND)).getId();

        return ChatMessage.builder()
                .userId(chatParticipant.getUser().getId())
                .photoId(photoId)
                .type(ChatMessageType.CHAT_MESSAGE)
                .messageType(chatMessageRequestPayload.getMessageType())
                .content(chatMessageRequestPayload.getContent())
                .chatroom(chatParticipant.getChatroom())
                .build();
    }
}
