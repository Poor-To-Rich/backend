package com.poortorich.chat.repository;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.UnreadChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnreadChatMessageRepository extends JpaRepository<UnreadChatMessage, Long> {
    List<UnreadChatMessage> findAllByChatMessage(ChatMessage chatMessage);
}
