package com.poortorich.chat.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("""
        SELECT c
          FROM Chatroom c
          JOIN ChatParticipant cp
            ON cp.chatroom = c
         WHERE cp.user = :user
           AND cp.role = :role
    """)
    List<Chatroom> findChatroomByUserAndRole(@Param("user") User user, @Param("role") ChatroomRole role);
}
