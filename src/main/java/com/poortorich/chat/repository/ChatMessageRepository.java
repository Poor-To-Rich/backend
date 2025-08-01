package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTopByChatroomOrderBySentAtDesc(Chatroom chatroom);
}
