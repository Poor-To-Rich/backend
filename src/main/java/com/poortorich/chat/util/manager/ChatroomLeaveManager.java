package com.poortorich.chat.util.manager;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
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

    public ChatroomClosedResponsePayload leaveChatroom(ChatParticipant participant) {
        if (participant.getChatroom().getIsClosed()) {
            return getClosedMessageOrDeleteAll(participant);
        }
        return null;
    }

    @Transactional
    public ChatroomClosedResponsePayload getClosedMessageOrDeleteAll(ChatParticipant participant) {
        Chatroom chatroom = participant.getChatroom();

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

        if (ChatroomRole.HOST.equals(participant.getRole())) {
            return chatMessageService.saveChatroomClosedMessage(chatroom);
        }
        return null;
    }
}
