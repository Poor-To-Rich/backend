package com.poortorich.chatnotice.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.service.ChatNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatNoticeFacade {

    private static final int PREVIEW_MAX_LENGTH = 30;

    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatNoticeService chatNoticeService;

    public LatestNoticeResponse getLatestNotice(String username, Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUsernameAndChatroom(username, chatroom);
        ChatNotice notice = chatNoticeService.getLatestNotice(chatroom);

        return buildLatestNoticeResponse(chatParticipant.getNoticeStatus(), notice);
    }

    private LatestNoticeResponse buildLatestNoticeResponse(NoticeStatus status, ChatNotice notice) {
        return LatestNoticeResponse.builder()
                .status(status.toString())
                .noticeId(notice.getId())
                .preview(truncateContent(notice.getContent()))
                .createdAt(notice.getCreatedDate().toString())
                .authorNickname(notice.getAuthor().getNickname())
                .build();
    }

    private String truncateContent(String content) {
        if (content.length() > PREVIEW_MAX_LENGTH) {
            return content.substring(0, PREVIEW_MAX_LENGTH);
        }

        return content;
    }
}
