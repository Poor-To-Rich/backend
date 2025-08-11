package com.poortorich.chatnotice.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chatnotice.entity.ChatNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatNoticeRepository extends JpaRepository<ChatNotice, Long> {

    Optional<ChatNotice> findTop1ByChatroomOrderByCreatedDateDesc(Chatroom chatroom);
}
