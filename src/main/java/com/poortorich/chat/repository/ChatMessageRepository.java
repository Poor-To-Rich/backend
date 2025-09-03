package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.user.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTopByChatroomOrderBySentAtDesc(Chatroom chatroom);

    List<ChatMessage> findAllByChatroom(Chatroom chatroom);

    void deleteByChatroom(Chatroom chatroom);

    Slice<ChatMessage> findByChatroomAndIdLessThanEqualAndSentAtAfterOrderByIdDesc(
            Chatroom chatroom,
            Long cursor,
            LocalDateTime joinedAt,
            Pageable pageable);

    Slice<ChatMessage> findByChatroomAndIdLessThanEqualAndSentAtBetweenOrderByIdDesc(
            Chatroom chatroom,
            Long cursor,
            LocalDateTime joinAt,
            LocalDateTime bannedAt,
            PageRequest pageRequest);

    boolean existsByContentAndMessageTypeAndChatroom(String content, MessageType messageType, Chatroom chatroom);

    Optional<ChatMessage> findTopByChatroomOrderByIdDesc(Chatroom chatroom);

    Optional<ChatMessage> findTopByChatroomAndTypeInOrderByIdDesc(
            Chatroom chatroom,
            Collection<ChatMessageType> chatMessage);

    @Query("""
            SELECT MAX(m.id)
            FROM ChatMessage m
            LEFT JOIN UnreadChatMessage u
                ON u.chatMessage = m
                AND u.user = :user
            WHERE m.chatroom = :chatroom
            AND u.id IS NULL
            """)
    Long findLatestReadMessageId(@Param("chatroom") Chatroom chatroom, @Param("user") User user);
}
