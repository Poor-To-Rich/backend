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
        if (!chatParticipant(user, chatroom).getIsParticipated()) {
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

    // TODO: 채팅방에서 역할이 멤버가 아니라면 예외를 발생
    public void validateIsMember(User user, Chatroom chatroom) {
    }

    // TODO: 채탕방에서 사용자가 차단되었다면 예외를 발생
    public void validateIsBanned(User user, Chatroom chatroom) {
    }

    // TODO: 채팅방에서 사용자가 퇴장한 상태이거나 없다면 예외를 발생
    public void validateHasLeft(User user, Chatroom chatroom) {
    }

    private ChatParticipant chatParticipant(User user, Chatroom chatroom) {
        return chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    private void validateIsParticipate(ChatParticipant chatParticipant) {
        if (!chatParticipant.getIsParticipated()) {
            throw new BadRequestException(ChatResponse.CHATROOM_NOT_PARTICIPATE);
        }
    }
}
