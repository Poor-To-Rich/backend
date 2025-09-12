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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatMessageService chatMessageService;
    private final ChatroomRepository chatroomRepository;
    private final RedisChatRepository redisChatRepository;

    private final ChatBuilder chatBuilder;

    public Chatroom createChatroom(String imageUrl, ChatroomCreateRequest request) {
        Chatroom chatroom = chatBuilder.buildChatroom(imageUrl, request);
        return chatroomRepository.save(chatroom);
    }

    public void overwriteChatroomsInRedis() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    overwriteChatroomsInRedisBySortBy(SortBy.UPDATED_AT);
                    overwriteChatroomsInRedisBySortBy(SortBy.LIKE);
                }
            });
        } else {
            overwriteChatroomsInRedisBySortBy(SortBy.UPDATED_AT);
            overwriteChatroomsInRedisBySortBy(SortBy.LIKE);
        }
    }

    private void overwriteChatroomsInRedisBySortBy(SortBy sortBy) {
        List<Long> chatrooms = getChatroomIdsBySortBy(sortBy);
        List<String> lastMessageTimes = getLastMessageTimes(chatrooms);

        if (!chatrooms.isEmpty()) {
            redisChatRepository.overwrite(sortBy, chatrooms, lastMessageTimes);
        }
    }

    public Slice<Chatroom> findByCursorSortByCreatedAt(Long cursor, Pageable pageable) {
        return chatroomRepository.findByCursorSortByCreatedAt(cursor, pageable);
    }

    public List<Chatroom> getAllChatrooms(SortBy sortBy, Long cursor) {
        if (redisChatRepository.existsBySortBy(sortBy)) {
            return findByIds(redisChatRepository.getChatroomIds(sortBy, cursor, 20));
        }

        saveChatroomsInRedisBySortBy(SortBy.UPDATED_AT);
        saveChatroomsInRedisBySortBy(SortBy.LIKE);

        return findByIds(redisChatRepository.getChatroomIds(sortBy, cursor, 20));
    }

    public List<String> getAllLastMessageTimes(SortBy sortBy, Long cursor) {
        return redisChatRepository.getLastMessageTimes(sortBy, cursor, 20);
    }

    private void saveChatroomsInRedisBySortBy(SortBy sortBy) {
        List<Long> chatrooms = getChatroomIdsBySortBy(sortBy);
        List<String> lastMessageTimes = getLastMessageTimes(chatrooms);

        if (!chatrooms.isEmpty()) {
            redisChatRepository.save(sortBy, chatrooms, lastMessageTimes);
        }
    }

    private List<Long> getChatroomIdsBySortBy(SortBy sortBy) {
        return getBySortBy(sortBy).stream()
                .map(Chatroom::getId)
                .toList();
    }

    private List<String> getLastMessageTimes(List<Long> chatroomIds) {
        return chatroomIds.stream()
                .map(chatroomId -> {
                    Chatroom chatroom = findById(chatroomId);
                    return chatMessageService.getLastMessageTime(chatroom);
                })
                .toList();
    }

    private List<Chatroom> getBySortBy(SortBy sortBy) {
        if (sortBy.equals(SortBy.LIKE)) {
            return chatroomRepository.findChatroomsSortByLike();
        }

        return chatroomRepository.findChatroomsSortByUpdatedAt();
    }

    private List<Chatroom> findByIds(List<Long> chatroomIds) {
        List<Chatroom> chatrooms = chatroomRepository.findAllByIdInAndIsClosedFalse(chatroomIds);
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

    public List<Chatroom> getChatroomsByRankingEnabledIsTrue() {
        return chatroomRepository.findAllByIsRankingEnabledIsTrueAndIsClosedIsFalse();
    }
}
