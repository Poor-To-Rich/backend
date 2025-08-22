package com.poortorich.chatnotice.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.repository.ChatNoticeRepository;
import com.poortorich.chatnotice.request.ChatNoticeCreateRequest;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public ChatNotice update(Long noticeId, ChatNoticeUpdateRequest noticeUpdateRequest) {
        ChatNotice chatNotice = findById(noticeId);
        chatNotice.updateContent(noticeUpdateRequest.getContent());
        return chatNoticeRepository.save(chatNotice);
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

    public boolean isLatestNotice(ChatNotice chatNotice) {
        Optional<ChatNotice> latestChatNotice = chatNoticeRepository.findTop1ByChatroomOrderByCreatedDateDesc(
                chatNotice.getChatroom());

        return latestChatNotice.isPresent() && latestChatNotice.get().getId().equals(chatNotice.getId());
    }

    public void delete(Long noticeId) {
        Optional<ChatNotice> chatNotice = chatNoticeRepository.findById(noticeId);
        chatNotice.ifPresent(chatNoticeRepository::delete);
    }

    public ChatNotice findNotice(Chatroom chatroom, Long noticeId) {
        return chatNoticeRepository.findByChatroomAndId(chatroom, noticeId)
                .orElseThrow(() -> new NotFoundException(ChatNoticeResponse.NOTICE_NOT_FOUND));
    }

    public ChatNotice findById(Long noticeId) {
        return chatNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException(ChatNoticeResponse.NOTICE_NOT_FOUND));
    }
}
