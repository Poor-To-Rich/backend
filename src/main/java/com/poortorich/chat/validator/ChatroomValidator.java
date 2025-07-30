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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatroomValidator {

    private final ChatParticipantRepository chatParticipantRepository;

    public void validateEnter(User user, Chatroom chatroom) {
        Optional<ChatParticipant> chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom);
        if (chatParticipant.isPresent()) {
            if (chatParticipant.get().getRole().equals(ChatroomRole.BANNED)) {
                throw new ForbiddenException(ChatResponse.CHATROOM_ENTER_DENIED);
            }
            if (chatParticipant.get().getIsParticipate()) {
                throw new ConflictException(ChatResponse.CHATROOM_ENTER_DUPLICATED);
            }
        }
    }

    public void validatePassword(Chatroom chatroom, String password) {
        if (!password.equals(chatroom.getPassword())) {
            throw new BadRequestException(ChatResponse.CHATROOM_PASSWORD_DO_NOT_MATCH);
        }
    }

    public void validateSubscribe(User user, Chatroom chatroom) {
        Optional<ChatParticipant> chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom);

        if (chatParticipant.isEmpty() || chatParticipant.get().getRole().equals(ChatroomRole.BANNED)) {
            throw new ForbiddenException(ChatResponse.CHATROOM_ENTER_DENIED);
        }
    }

    public void validateParticipate(User user, Chatroom chatroom) {
        Optional<ChatParticipant> chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom);

        if (chatParticipant.isEmpty() || !chatParticipant.get().getIsParticipate()) {
            throw new ForbiddenException(ChatResponse.CHATROOM_LEAVE_ALREADY);
        }
    }
}
