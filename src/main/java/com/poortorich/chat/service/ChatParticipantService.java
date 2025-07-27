package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    public void createChatroomHost(User user, Chatroom chatroom) {
        ChatParticipant chatParticipant = ChatBuilder.buildChatParticipant(user, ChatroomRole.HOST, chatroom);
        chatParticipantRepository.save(chatParticipant);
    }
}
