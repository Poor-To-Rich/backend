package com.poortorich.chatnotice.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.repository.ChatNoticeRepository;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatNoticeService {

    private final ChatNoticeRepository chatNoticeRepository;

    public ChatNotice getLatestNotice(Chatroom chatroom) {
        return chatNoticeRepository.findTop1ByChatroomOrderByCreatedDateDesc(chatroom)
                .orElseThrow(() -> new NotFoundException(ChatNoticeResponse.NOTICE_NOT_FOUND));
    }
}
