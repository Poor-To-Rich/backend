package com.poortorich.chatnotice.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.request.ChatNoticeRequestPayload;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.repository.ChatNoticeRepository;
import com.poortorich.chatnotice.request.ChatNoticeCreateRequest;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatNoticeService {

    private final ChatNoticeRepository chatNoticeRepository;

    public ChatNotice create(ChatParticipant author, ChatNoticeCreateRequest noticeCreateRequest) {
        ChatNotice notice = ChatNotice.builder()
                .content(noticeCreateRequest.getContent())
                .author(author)
                .chatroom(author.getChatroom())
                .build();

        return chatNoticeRepository.save(notice);
    }

    public ChatNotice getLatestNotice(Chatroom chatroom) {
        return chatNoticeRepository.findTop1ByChatroomOrderByCreatedDateDesc(chatroom)
                .orElse(null);
    }

    public List<ChatNotice> getPreviewNotices(Chatroom chatroom) {
        return chatNoticeRepository.findTop3ByChatroomOrderByCreatedDateDesc(chatroom);
    }

    public ChatNotice update(PayloadContext context, ChatNoticeRequestPayload requestPayload) {
        ChatNotice chatNotice = chatNoticeRepository.findTop1ByChatroomOrderByCreatedDateDesc(context.chatroom())
                .orElse(null);

        if (Objects.isNull(chatNotice)) {
            return chatNotice;
        }

        chatNotice.updateContent(requestPayload.getContent());

        return chatNoticeRepository.save(chatNotice);
    }

    public ChatNotice delete(PayloadContext context) {
        Optional<ChatNotice> chatNotice = chatNoticeRepository.findTop1ByChatroomOrderByCreatedDateDesc(context.chatroom());
        chatNotice.ifPresent(chatNoticeRepository::delete);
        return null;
    }

    public ChatNotice findNotice(Chatroom chatroom, Long noticeId) {
        return chatNoticeRepository.findByChatroomAndId(chatroom, noticeId)
                .orElseThrow(() -> new NotFoundException(ChatNoticeResponse.NOTICE_NOT_FOUND));
    }
}
