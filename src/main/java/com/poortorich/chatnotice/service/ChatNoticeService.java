package com.poortorich.chatnotice.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.request.ChatNoticeRequestPayload;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.repository.ChatNoticeRepository;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatNoticeService {

    private final ChatNoticeRepository chatNoticeRepository;

    public ChatNotice create(PayloadContext context, ChatNoticeRequestPayload requestPayload) {
        if (Objects.isNull(requestPayload.getContent())) {
            throw new BadRequestException(ChatNoticeResponse.CONTENT_REQUIRED);
        }

        ChatNotice notice = ChatNotice.builder()
                .content(requestPayload.getContent())
                .author(context.chatParticipant())
                .chatroom(context.chatroom())
                .build();

        return chatNoticeRepository.save(notice);
    }

    public Slice<ChatNotice> getAllNoticeByCursor(Chatroom chatroom, Long cursor, Pageable pageable) {
        return chatNoticeRepository.findByChatroomAndIdLessThanOrderByIdDesc(chatroom, cursor, pageable);
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

    public ChatNotice handleChatNotice(PayloadContext context, ChatNoticeRequestPayload requestPayload) {
        return switch (requestPayload.getNoticeType()) {
            case CREATE -> create(context, requestPayload);
            case UPDATE -> update(context, requestPayload);
            case DELETE -> delete(context);
        };
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
