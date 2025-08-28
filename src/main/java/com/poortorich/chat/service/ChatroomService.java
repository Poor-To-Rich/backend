package com.poortorich.chat.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.repository.ChatroomRepository;
import com.poortorich.chat.repository.RedisChatRepository;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.enums.SortBy;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final RedisChatRepository redisChatRepository;

    public Chatroom createChatroom(String imageUrl, ChatroomCreateRequest request) {
        Chatroom chatroom = ChatBuilder.buildChatroom(imageUrl, request);
        return chatroomRepository.save(chatroom);
    }

    public List<Chatroom> getAllChatrooms(SortBy sortBy, Long cursor) {
        if (redisChatRepository.existsBySortBy(sortBy)) {
            return findByIds(redisChatRepository.getChatroomIds(sortBy, cursor, 20));
        }

        List<Long> chatrooms = getBySortBy(sortBy).stream()
                .map(Chatroom::getId)
                .toList();
        redisChatRepository.save(sortBy, chatrooms);
        return findByIds(redisChatRepository.getChatroomIds(sortBy, cursor, 20));
    }

    private List<Chatroom> getBySortBy(SortBy sortBy) {
        if (sortBy.equals(SortBy.LIKE)) {
            return chatroomRepository.findChatroomsSortByLike();
        }

        if (sortBy.equals(SortBy.CREATED_AT)) {
            return chatroomRepository.findChatroomsSortByCreatedAt();
        }

        return chatroomRepository.findChatroomsSortByUpdatedAt();
    }

    private List<Chatroom> findByIds(List<Long> chatroomIds) {
        List<Chatroom> chatrooms = chatroomRepository.findAllById(chatroomIds);
        chatrooms.sort(Comparator.comparingInt(c -> chatroomIds.indexOf(c.getId())));
        return chatrooms;
    }

    public Boolean hasNext(SortBy sortBy, Long lastChatroomId) {
        return redisChatRepository.hasNext(sortBy, lastChatroomId);
    }

    public Long getNextCursor(SortBy sortBy, Long lastChatroomId) {
        return redisChatRepository.getNextCursor(sortBy, lastChatroomId);
    }

    public Chatroom findById(Long chatroomId) {
        return chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHATROOM_NOT_FOUND));
    }

    public List<Chatroom> searchChatrooms(String keyword) {
        return chatroomRepository.searchChatrooms(keyword);
    }

    public List<Chatroom> getHostedChatrooms(User user) {
        return chatroomRepository.findChatroomByUserAndRole(user, ChatroomRole.HOST);
    }

    @Transactional
    public void closeChatroomById(Long chatroomId) {
        chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHATROOM_NOT_FOUND))
                .closeChatroom();
    }

    @Transactional
    public void deleteById(Long chatroomId) {
        chatroomRepository.deleteById(chatroomId);
    }

    public Long getFirstChatroomIdByUser(User user) {
        return chatroomRepository.findFirstChatroomIdByUser(user.getId());
    }
}
