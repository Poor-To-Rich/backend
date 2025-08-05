package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    public void createChatroomHost(User user, Chatroom chatroom) {
        ChatParticipant chatParticipant = ChatBuilder.buildChatParticipant(user, ChatroomRole.HOST, chatroom);
        chatParticipantRepository.save(chatParticipant);
    }

    public void enterUser(User user, Chatroom chatroom) {
        ChatParticipant chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseGet(() -> ChatBuilder.buildChatParticipant(user, ChatroomRole.MEMBER, chatroom));

        chatParticipant.restoreParticipation();

        chatParticipantRepository.save(chatParticipant);
    }

    public ChatParticipant findByUserAndChatroom(User user, Chatroom chatroom) {
        return chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    public Long countByChatroom(Chatroom chatroom) {
        return chatParticipantRepository.countByChatroomAndIsParticipateTrue(chatroom);
    }

    public List<ChatParticipant> findAllByChatroom(Chatroom chatroom) {
        return chatParticipantRepository.findAllByChatroomAndIsParticipateTrue(chatroom);
    }

    public boolean isAllParticipantLeft(Chatroom chatroom) {
        List<ChatParticipant> participants = chatParticipantRepository.findAllByChatroom(chatroom);
        return participants.stream()
                .noneMatch(ChatParticipant::getIsParticipate);
    }

    @Transactional
    public void deleteAllByChatroom(Chatroom chatroom) {
        chatParticipantRepository.deleteAllByChatroom(chatroom);
    }

    public List<ChatParticipant> findAllByChatroomExcludingUser(Chatroom chatroom, User excludedUser) {
        return chatParticipantRepository.findAllByChatroomAndIsParticipateTrueAndUserNot(chatroom, excludedUser);
    }
}
