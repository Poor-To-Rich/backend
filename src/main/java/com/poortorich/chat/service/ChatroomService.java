package com.poortorich.chat.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.repository.ChatroomRepository;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.util.ChatBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;

    public Chatroom createChatroom(String imageUrl, ChatroomCreateRequest request) {
        Chatroom chatroom = ChatBuilder.buildChatroom(imageUrl, request);
        return chatroomRepository.save(chatroom);
    }
}
