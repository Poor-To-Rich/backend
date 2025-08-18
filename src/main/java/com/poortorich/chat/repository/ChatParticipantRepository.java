package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.user.entity.User;
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

    Long countByChatroomAndIsParticipatedTrue(Chatroom chatroom);

    @Query("""
            SELECT cp
            FROM ChatParticipant cp
            WHERE cp.chatroom = :chatroom
            AND cp.role = 'HOST'
            """)
    ChatParticipant getChatroomHost(@Param("chatroom") Chatroom chatroom);

    List<ChatParticipant> findAllByChatroomAndIsParticipatedTrue(Chatroom chatroom);

    void deleteAllByChatroom(Chatroom chatroom);

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
    List<ChatParticipant> findAllByUsernameWithChatroomAndUser(String username);
}
