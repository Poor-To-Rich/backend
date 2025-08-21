package com.poortorich.chatnotice.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.model.NoticeCreateResult;
import com.poortorich.chatnotice.model.NoticeUpdateResult;
import com.poortorich.chatnotice.request.ChatNoticeCreateRequest;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.chatnotice.response.AllNoticesResponse;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.response.NoticeDetailsResponse;
import com.poortorich.chatnotice.response.PreviewNoticesResponse;
import com.poortorich.chatnotice.service.ChatNoticeService;
import com.poortorich.chatnotice.util.ChatNoticeBuilder;
import com.poortorich.chatnotice.util.mapper.ChatNoticeDataMapper;
import com.poortorich.chatnotice.validator.ChatNoticeValidator;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatNoticeFacade {

    private static final int PAGEABLE_SIZE = 20;

    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatNoticeService chatNoticeService;
    private final UserService userService;

    private final ChatNoticeDataMapper noticeDataMapper;
    private final ChatParticipantValidator participantValidator;
    private final ChatNoticeValidator chatNoticeValidator;

    @Transactional
    public NoticeCreateResult create(String username, Long chatroomId, ChatNoticeCreateRequest noticeCreateRequest) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        participantValidator.validateIsHost(chatParticipant);

        ChatNotice newChatNotice = chatNoticeService.create(chatParticipant, noticeCreateRequest);
        NoticeCreateResult noticeCreateResult = noticeDataMapper.mapToNoticeCreateResult(newChatNotice);

        List<ChatParticipant> participants = chatParticipantService.findAllByChatroom(chatroom);
        chatParticipantService.updateAllNoticeStatus(participants, NoticeStatus.DEFAULT);

        return noticeCreateResult;
    }

    @Transactional
    public NoticeUpdateResult update(
            String username,
            Long chatroomId,
            Long noticeId,
            ChatNoticeUpdateRequest noticeUpdateRequest
    ) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        participantValidator.validateIsHost(chatParticipant);
        chatNoticeValidator.validateNoticeBelongsToChatroom(noticeId, chatroom);

        ChatNotice chatNotice = chatNoticeService.update(noticeId, noticeUpdateRequest);
        boolean isLatestNotice = chatNoticeService.isLatestNotice(chatNotice);
        NoticeUpdateResult result = noticeDataMapper.mapToNoticeUpdateResult(chatNotice, isLatestNotice);

        if (isLatestNotice) {
            List<ChatParticipant> participants = chatParticipantService.findAllByChatroom(chatroom);
            chatParticipantService.updateAllNoticeStatus(participants, NoticeStatus.DEFAULT);
        }
        return result;
    }

    public AllNoticesResponse getAllNotices(Long chatroomId, Long cursor) {
        Chatroom chatroom = chatroomService.findById(chatroomId);

        Pageable pageable = PageRequest.of(0, PAGEABLE_SIZE);
        Slice<ChatNotice> chatNotices = chatNoticeService.getAllNoticeByCursor(chatroom, cursor, pageable);

        List<ChatNotice> contents = chatNotices.getContent();
        Long lastId = contents.isEmpty() ? null : contents.getLast().getId();

        return ChatNoticeBuilder.buildAllNoticesResponse(
                chatNotices.hasNext(),
                getNextCursor(chatNotices.hasNext(), lastId),
                contents.isEmpty() ? null : contents
        );
    }

    private Long getNextCursor(Boolean hasNext, Long lastId) {
        if (!hasNext) {
            return null;
        }

        return lastId;
    }

    public LatestNoticeResponse getLatestNotice(String username, Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUsernameAndChatroom(username, chatroom);
        ChatNotice notice = chatNoticeService.getLatestNotice(chatroom);

        return ChatNoticeBuilder.buildLatestNoticeResponse(chatParticipant.getNoticeStatus(), notice);
    }

    @Transactional(readOnly = true)
    public NoticeDetailsResponse getNoticeDetails(Long chatroomId, Long noticeId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatNotice notice = chatNoticeService.findNotice(chatroom, noticeId);

        return ChatNoticeBuilder.buildNoticeDetailsResponse(notice);
    }

    public PreviewNoticesResponse getPreviewNotices(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        List<ChatNotice> previewNotice = chatNoticeService.getPreviewNotices(chatroom);

        return PreviewNoticesResponse.builder()
                .notices(ChatNoticeBuilder.buildPreviewNoticeResponse(previewNotice))
                .build();
    }

    @Transactional
    public BasePayload deleteNotice(String username, Long chatroomId, Long noticeId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        participantValidator.validateIsHost(chatParticipant);
        chatNoticeValidator.validateNoticeBelongsToChatroom(noticeId, chatroom);

        ChatNotice chatNotice = chatNoticeService.findById(noticeId);
        boolean isLatestNotice = chatNoticeService.isLatestNotice(chatNotice);
        chatNoticeService.delete(noticeId);

        if (isLatestNotice) {
            List<ChatParticipant> participants = chatParticipantService.findAllByChatroom(chatroom);
            chatParticipantService.updateAllNoticeStatus(participants, NoticeStatus.PERMANENT_HIDDEN);
        }

        return noticeDataMapper.mapToNoticeDeletePayload(isLatestNotice);
    }
}
