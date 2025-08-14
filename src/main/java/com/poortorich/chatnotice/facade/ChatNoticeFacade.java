package com.poortorich.chatnotice.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.response.PreviewNoticesResponse;
import com.poortorich.chatnotice.service.ChatNoticeService;
import com.poortorich.chatnotice.util.ChatNoticeBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatNoticeFacade {

    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatNoticeService chatNoticeService;

    public LatestNoticeResponse getLatestNotice(String username, Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUsernameAndChatroom(username, chatroom);
        ChatNotice notice = chatNoticeService.getLatestNotice(chatroom);

        return ChatNoticeBuilder.buildLatestNoticeResponse(chatParticipant.getNoticeStatus(), notice);
    }

    public PreviewNoticesResponse getPreviewNotices(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        List<ChatNotice> previewNotice = chatNoticeService.getPreviewNotices(chatroom);

        return PreviewNoticesResponse.builder()
                .notices(ChatNoticeBuilder.buildPreviewNoticeResponse(previewNotice))
                .build();
    }
}
