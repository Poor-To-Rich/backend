package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    Optional<ChatParticipant> findByUserAndChatroom(User user, Chatroom chatroom);

    @Query("""
                SELECT cp
                  FROM ChatParticipant cp
                 WHERE cp.user.username = :username
                   AND cp.chatroom = :chatroom
            """)
    Optional<ChatParticipant> findByUsernameAndChatroom(
            @Param("username") String username,
            @Param("chatroom") Chatroom chatroom
    );

    @Query("""
                SELECT cp
                  FROM ChatParticipant cp
                 WHERE cp.user.id = :userId
                   AND cp.chatroom = :chatroom
            """)
    Optional<ChatParticipant> findByUserIdAndChatroom(
            @Param("userId") Long userId,
            @Param("chatroom") Chatroom chatroom
    );

    @Query("""
            SELECT COUNT(cp)
            FROM ChatParticipant cp
            WHERE cp.chatroom = :chatroom
            AND cp.isParticipated = true
            AND cp.role <> 'BANNED'
            """)
    Long countParticipantsByChatroom(@Param("chatroom") Chatroom chatroom);

    @Query("""
            SELECT cp
            FROM ChatParticipant cp
            WHERE cp.chatroom = :chatroom
            AND cp.role = 'HOST'
            """)
    ChatParticipant getChatroomHost(@Param("chatroom") Chatroom chatroom);

    List<ChatParticipant> findAllByChatroomAndIsParticipatedTrue(Chatroom chatroom);

    void deleteByChatroom(Chatroom chatroom);

    List<ChatParticipant> findAllByChatroom(Chatroom chatroom);

    @Query("""
            SELECT cp
            FROM ChatParticipant cp
            JOIN FETCH cp.user
            WHERE cp.chatroom = :chatroom
            AND cp.isParticipated = true
            AND cp.user != :excludedUser
            """)
    List<ChatParticipant> findAllByChatroomAndIsParticipatedTrueAndUserNot(Chatroom chatroom, User excludedUser);

    List<ChatParticipant> findAllByUserAndIsParticipatedTrue(User user);

    @Query("""
            SELECT cp FROM ChatParticipant cp
            JOIN FETCH cp.chatroom cr
            JOIN FETCH cp.user u
            WHERE u.username = :username
            """)
    List<ChatParticipant> findAllByUsernameWithChatroomAndUser(@Param("username") String username);

    @Query("""
                SELECT cp
                FROM ChatParticipant cp
                JOIN FETCH cp.user u
                WHERE cp.chatroom = :chatroom
                  AND cp.isParticipated = true
                ORDER BY
                  CASE WHEN cp.role = com.poortorich.chat.entity.enums.ChatroomRole.HOST THEN 0 ELSE 1 END,
                  u.nickname ASC
            """)
    List<ChatParticipant> findAllOrderedParticipants(@Param("chatroom") Chatroom chatroom);

    @Query("""
            SELECT cp
            FROM ChatParticipant cp
            JOIN FETCH cp.user u
            JOIN FETCH cp.chatroom c
            WHERE c.id = :chatroomId
            AND u.username = :username
            AND cp.isParticipated = true
            """)
    Optional<ChatParticipant> findByUsernameAndChatroomId(
            @Param("username") String username,
            @Param("chatroomId") Long chatroomId);

    @Query("""
            SELECT cp
            FROM ChatParticipant cp
            JOIN FETCH cp.user u
            JOIN FETCH cp.chatroom c
            WHERE c.id = :chatroomId
            AND u.id = :userId
            AND cp.isParticipated = true
            """)
    Optional<ChatParticipant> findByUserIdAndChatroomId(
            @Param("userId") Long userId,
            @Param("chatroomId") Long chatroomId);

    @Query("""
                SELECT cp
                  FROM ChatParticipant cp
                  JOIN FETCH cp.user u
                 WHERE cp.chatroom = :chatroom
                   AND cp.isParticipated = true
                   AND LOWER(u.nickname) LIKE CONCAT('%', LOWER(:nickname), '%')
                 ORDER BY u.nickname ASC
            """)
    List<ChatParticipant> searchByChatroomAndNickname(
            @Param("chatroom") Chatroom chatroom,
            @Param("nickname") String nickname
    );

    @Query(value = """
            SELECT cp.*,
                COALESCE(
                    lm.last_sent_at,
                    CASE WHEN cp.role = 'HOST' THEN cr.created_date ELSE cp.join_at END
                ) as latest_message_time
            FROM chat_participant cp
            JOIN chatroom cr ON cp.chatroom_id = cr.id
            JOIN user u ON cp.user_id = u.id
            LEFT JOIN (
                SELECT chatroom_id, MAX(sent_at) as last_sent_at
                FROM chat_message
                WHERE type IN ('CHAT_MESSAGE', 'RANKING_MESSAGE')
                GROUP BY chatroom_id
            ) lm ON cr.id = lm.chatroom_id
            WHERE u.id = :userId
            AND cp.is_participated = true
            AND (:cursor IS NULL OR COALESCE(
                    lm.last_sent_at,
                    CASE WHEN cp.role = 'HOST' THEN cr.created_date ELSE cp.join_at END) < :cursor
                )
            ORDER BY latest_message_time DESC
            LIMIT :#{#pageable.pageSize + 1}
            """, nativeQuery = true)
    List<ChatParticipant> findMyParticipants(
            @Param("userId") Long userId,
            @Param("cursor") LocalDateTime cursor,
            Pageable pageable);

    Optional<ChatParticipant> findByChatroomAndRankingStatus(Chatroom chatroom, RankingStatus rankingStatus);
}
