package com.poortorich.chat.util.manager;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.service.UnreadChatMessageService;
import com.poortorich.chatnotice.service.ChatNoticeService;
import com.poortorich.photo.service.PhotoService;
import com.poortorich.ranking.service.RankingService;
import com.poortorich.report.service.ReportService;
import com.poortorich.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ChatroomLeaveManager {

    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatMessageService chatMessageService;
    private final TagService tagService;
    private final PhotoService photoService;
    private final RankingService rankingService;
    private final ChatNoticeService chatNoticeService;
    private final UnreadChatMessageService unreadChatMessageService;
    private final ReportService reportService;

    public BasePayload leaveChatroom(PayloadContext payloadContext) {
        if (payloadContext.chatroom().getIsClosed()) {
            return getClosedMessageOrDeleteAll(payloadContext);
        }
        return null;
    }

    @Transactional
    private BasePayload getClosedMessageOrDeleteAll(PayloadContext payloadContext) {
        Chatroom chatroom = payloadContext.chatroom();
        ChatParticipant participant = payloadContext.chatParticipant();

        if (chatParticipantService.isAllParticipantLeft(chatroom)) {
            tagService.deleteAllByChatroom(chatroom);
            unreadChatMessageService.deleteAllByChatroom(chatroom);
            chatMessageService.deleteAllByChatroom(chatroom);
            photoService.deleteAllByChatroom(chatroom);
            rankingService.deleteAllByChatroom(chatroom);
            chatNoticeService.deleteAllByChatroom(chatroom);
            reportService.deleteAllByChatroom(chatroom);
            chatParticipantService.deleteAllByChatroom(chatroom);
            chatroomService.deleteById(chatroom.getId());
            return null;
        }

        if (participant.getRole().equals(ChatroomRole.HOST)) {
            return chatMessageService.saveChatroomClosedMessage(chatroom).mapToBasePayload();
        }
        return null;
    }
}
