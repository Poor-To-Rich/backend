package com.poortorich.chatnotice.validator;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chatnotice.repository.ChatNoticeRepository;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatNoticeValidator {

    private final ChatNoticeRepository chatNoticeRepository;

    public void validateNoticeBelongsToChatroom(Long noticeId, Chatroom chatroom) {
        if (!chatNoticeRepository.existsByIdAndChatroom(noticeId, chatroom)) {
            throw new BadRequestException(ChatNoticeResponse.CHATROOM_NOTICE_MISMATCH);
        }
    }
}
