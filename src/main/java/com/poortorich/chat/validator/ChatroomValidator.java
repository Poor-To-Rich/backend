package com.poortorich.chat.validator;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.global.exceptions.ForbiddenException;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatroomValidator {

    private final ChatParticipantRepository chatParticipantRepository;

    public void validateEnter(User user, Chatroom chatroom) {
        if (chatroom.getIsClosed()) {
            throw new BadRequestException(ChatResponse.CHATROOM_IS_CLOSED);
        }

        Optional<ChatParticipant> chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom);
        if (chatParticipant.isPresent()) {
            if (chatParticipant.get().getRole().equals(ChatroomRole.BANNED)) {
                throw new ForbiddenException(ChatResponse.CHATROOM_ENTER_DENIED);
            }
            if (chatParticipant.get().getIsParticipated()) {
                throw new ConflictException(ChatResponse.CHATROOM_ENTER_DUPLICATED);
            }
        }

        long currentMemberCount = chatParticipantRepository.countByChatroomAndIsParticipatedTrue(chatroom);
        if (currentMemberCount == chatroom.getMaxMemberCount()) {
            throw new ForbiddenException(ChatResponse.CHATROOM_ENTER_DENIED);
        }
    }

    public void validatePassword(Chatroom chatroom, String password) {
        String chatroomPassword = chatroom.getPassword();
        if (!Objects.equals(chatroomPassword, password)) {
            throw new BadRequestException(ChatResponse.CHATROOM_PASSWORD_DO_NOT_MATCH);
        }
    }

    public void validateSubscribe(User user, Chatroom chatroom) {
        Optional<ChatParticipant> chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom);

        if (chatParticipant.isEmpty() || chatParticipant.get().getRole().equals(ChatroomRole.BANNED)) {
            throw new ForbiddenException(ChatResponse.CHATROOM_ENTER_DENIED);
        }
    }

    public void validateCanUpdateMaxMemberCount(Chatroom chatroom, Long maxMemberCount) {
        Long currentMemberCount = chatParticipantRepository.countByChatroomAndIsParticipatedTrue(chatroom);
        if (currentMemberCount > maxMemberCount) {
            throw new BadRequestException(ChatResponse.CHATROOM_MAX_MEMBER_COUNT_EXCEED);
        }
    }

    public void validateParticipate(User user, Chatroom chatroom) {
        Optional<ChatParticipant> chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom);

        if (chatParticipant.isEmpty() || !chatParticipant.get().getIsParticipated()) {
            throw new BadRequestException(ChatResponse.CHATROOM_LEAVE_ALREADY);
        }
    }

    public void validateIsOpened(Chatroom chatroom) {
        if (chatroom.getIsClosed()) {
            throw new BadRequestException(ChatResponse.CHATROOM_IS_CLOSED);
        }
    }
}
