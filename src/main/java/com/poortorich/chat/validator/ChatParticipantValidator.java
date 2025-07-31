package com.poortorich.chat.validator;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatParticipantValidator {

    private final ChatParticipantRepository chatParticipantRepository;

    public void validateIsParticipate(User user, Chatroom chatroom) {
        if (!chatParticipant(user, chatroom).getIsParticipate()) {
            throw new BadRequestException(ChatResponse.CHATROOM_NOT_PARTICIPATE);
        }
    }

    public void validateIsHost(User user, Chatroom chatroom) {
        ChatParticipant chatParticipant = chatParticipant(user, chatroom);
        validateIsParticipate(chatParticipant);
        if (!ChatroomRole.HOST.equals(chatParticipant.getRole())) {
            throw new BadRequestException(ChatResponse.CHAT_PARTICIPANT_NOT_HOST);
        }
    }

    public void validateIsMember(User user, Chatroom chatroom) {
    }

    public void validateIsBanned(User user, Chatroom chatroom) {
    }

    public void validateHasLeft(User user, Chatroom chatroom) {
    }

    private ChatParticipant chatParticipant(User user, Chatroom chatroom) {
        return chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    private void validateIsParticipate(ChatParticipant chatParticipant) {
        if (!chatParticipant.getIsParticipate()) {
            throw new BadRequestException(ChatResponse.CHATROOM_NOT_PARTICIPATE);
        }
    }
}
