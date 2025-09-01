package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.UnreadChatMessage;
import com.poortorich.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnreadChatMessageRepository extends JpaRepository<UnreadChatMessage, Long> {
    List<UnreadChatMessage> findAllByChatMessage(ChatMessage chatMessage);

    @Query("""
            SELECT MAX(u.chatMessage.id)
            FROM UnreadChatMessage u
            WHERE u.chatroom = :chatroom
            AND u.user = :user
            """)
    Long findLastUnreadMessageId(@Param("chatroom") Chatroom chatroom, @Param("user") User user);

    @Query("""
            SELECT MAX(u.chatMessage.id)
            FROM UnreadChatMessage u
            WHERE u.chatroom IN :chatrooms
            AND u.user IN :users
            GROUP BY u.chatroom, u.user
            """)
    List<Long> findLastUnreadMessageIds(@Param("chatrooms") List<Chatroom> chatrooms, @Param("users") List<User> user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM UnreadChatMessage u
            WHERE u.chatroom = :chatroom
            AND u.user = :user
            """)
    void markMessagesAsRead(@Param("chatroom") Chatroom chatroom, @Param("user") User user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM UnreadChatMessage u
            WHERE u.chatroom IN :chatrooms
            AND u.user IN :users
            """)
    void markAllMessageAsRead(@Param("chatrooms") List<Chatroom> chatrooms, @Param("users") List<User> users);

    void deleteByChatroom(Chatroom chatroom);

    Long countByUserAndChatroom(User user, Chatroom chatroom);

}
