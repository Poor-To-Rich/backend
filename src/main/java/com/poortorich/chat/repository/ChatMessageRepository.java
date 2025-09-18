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

    Slice<ChatMessage> findByChatroomAndIdLessThanEqualAndIdBetweenOrderByIdDesc(
            Chatroom chatroom,
            Long cursor,
            Long enterMessageId,
            Long kickMessageId,
            Pageable pageable);

    Slice<ChatMessage> findByChatroomAndIdLessThanEqualAndIdGreaterThanEqualOrderByIdDesc(
            Chatroom chatroom,
            Long cursor,
            Long enterMessageId,
            PageRequest pageRequest);

    boolean existsByContentAndMessageTypeAndChatroom(String content, MessageType messageType, Chatroom chatroom);

    Optional<ChatMessage> findTopByChatroomAndTypeInOrderByIdDesc(Chatroom chatroom, Collection<ChatMessageType> types);

    Optional<ChatMessage> findTopByChatroomAndTypeInAndSentAtAfterOrderByIdDesc(
            Chatroom chatroom,
            Collection<ChatMessageType> chatMessage,
            LocalDateTime joinAt);

    @Query("""
            SELECT MAX(m.id)
            FROM ChatMessage m
            LEFT JOIN UnreadChatMessage u
                ON u.chatMessage = m
                AND u.user = :user
            WHERE m.chatroom = :chatroom
            AND m.type = :messageType
            AND m.sentAt >= :joinAt
            AND u.id IS NULL
            """)
    Long findLatestReadMessageId(
            @Param("chatroom") Chatroom chatroom,
            @Param("user") User user,
            @Param("joinAt") LocalDateTime joinAt,
            @Param("messageType") ChatMessageType messageType);

    @Query("""
            SELECT MAX(m.id)
            FROM ChatMessage m
            LEFT JOIN UnreadChatMessage u
                ON u.chatMessage = m
                AND u.user = :user
            WHERE m.chatroom = :chatroom
            AND m.type = :messageType
            AND m.sentAt >= :joinAt
            AND m.sentAt <= :bannedAt
            AND u.id IS NULL
            """)
    Long findLatestReadMessageId(
            @Param("chatroom") Chatroom chatroom,
            @Param("user") User user,
            @Param("joinAt") LocalDateTime joinAt,
            @Param("bannedAt") LocalDateTime bannedAt,
            @Param("messageType") ChatMessageType messageType);

    Optional<ChatMessage> findTopByChatroomAndTypeInAndSentAtLessThanEqualOrderByIdDesc(
            Chatroom chatroom,
            List<ChatMessageType> chatMessage,
            LocalDateTime bannedAt);

    Slice<ChatMessage> findByChatroomAndIdLessThanEqualAndSentAtGreaterThanOrderByIdDesc(
            Chatroom chatroom,
            Long cursor,
            LocalDateTime joinAt,
            PageRequest pageRequest);

    Slice<ChatMessage> findByChatroomAndIdLessThanEqualAndSentAtBetweenOrderByIdDesc(
            Chatroom chatroom,
            Long cursor,
            LocalDateTime joinAt,
            LocalDateTime bannedAt,
            PageRequest pageRequest);

    @Query("""
            SELECT MAX(m.id)
            FROM ChatMessage m
            WHERE m.chatroom = :chatroom
            """)
    Long findLatestMessageIdByChatroom(Chatroom chatroom);

    @Query(value = """
            SELECT MAX(
                COALESCE(
                    cm.sent_at,
                    CASE when cp.role = 'HOST' THEN cr.created_date ELSE cp.join_at END
                )
            ) as latest_time
            FROM chat_participant cp
            JOIN chatroom cr ON cp.chatroom_id = cr.id
            LEFT JOIN chat_message cm ON (
                    cm.chatroom_id = cr.id
                    AND cm.type IN ('CHAT_MESSAGE', 'RANKING_MESSAGE')
                    AND cm.id = (
                        SELECT MAX(cm2.id)
                        FROM chat_message cm2
                        WHERE cm2.chatroom_id = cr.id
                        AND cm2.type IN ('CHAT_MESSAGE', 'RANKING_MESSAGE')
                    )
            )
            WHERE cp.user_id = :#{#user.id}
            AND cp.is_participated = true
            """, nativeQuery = true)
    Optional<LocalDateTime> findLatestMessageTimeByUser(User user);
}
