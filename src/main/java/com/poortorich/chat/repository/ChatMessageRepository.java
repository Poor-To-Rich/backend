package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.MessageType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTopByChatroomOrderBySentAtDesc(Chatroom chatroom);

    List<ChatMessage> findAllByChatroom(Chatroom chatroom);

    void deleteAllByChatroom(Chatroom chatroom);

    Slice<ChatMessage> findByChatroomAndIdLessThanEqualAndSentAtAfterOrderByIdDesc(
            Chatroom chatroom,
            Long cursor,
            LocalDateTime joinedAt,
            Pageable pageable);

    boolean existsByContentAndMessageTypeAndChatroom(String content, MessageType messageType, Chatroom chatroom);
}
