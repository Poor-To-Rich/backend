package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTopByChatroomOrderBySentAtDesc(Chatroom chatroom);

    List<ChatMessage> findAllByChatroom(Chatroom chatroom);

    void deleteAllByChatroom(Chatroom chatroom);

    Slice<ChatMessage> findByChatroomAndIdLessThanOrderByIdDesc(Chatroom chatroom, Long cursor, Pageable pageable);
}
