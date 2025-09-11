package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query("""
            SELECT cp
            FROM ChatParticipant cp
            JOIN FETCH cp.chatroom cr
            JOIN FETCH cp.user u
            WHERE u = :user
            AND cp.isParticipated = true
            AND cr.id >= :cursor
            ORDER BY cr.id ASC
            """)
    Slice<ChatParticipant> findMyParticipants(@Param("user") User user, @Param("cursor") Long cursor, Pageable pageable);

    Optional<ChatParticipant> findByChatroomAndRankingStatus(Chatroom chatroom, RankingStatus rankingStatus);
}
