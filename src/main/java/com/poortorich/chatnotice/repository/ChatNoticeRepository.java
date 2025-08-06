package com.poortorich.chatnotice.repository;

import com.poortorich.chatnotice.entity.ChatNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatNoticeRepository extends JpaRepository<ChatNotice, Long> {
}
