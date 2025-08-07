package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.chat.response.ChatParticipantProfile;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    public void createChatroomHost(User user, Chatroom chatroom) {
        ChatParticipant chatParticipant = ChatBuilder.buildChatParticipant(user, ChatroomRole.HOST, chatroom);
        chatParticipantRepository.save(chatParticipant);
    }

    public void updateNoticeStatus(String username, Chatroom chatroom, ChatNoticeUpdateRequest request) {
        ChatParticipant chatParticipant = findByUsernameAndChatroom(username, chatroom);
        validateNoticeStatus(chatParticipant.getNoticeStatus());
        chatParticipant.updateNoticeStatus(request.parseStatus());
        chatParticipantRepository.save(chatParticipant);
    }

    private void validateNoticeStatus(NoticeStatus noticeStatus) {
        if (noticeStatus.equals(NoticeStatus.PERMANENT_HIDDEN)) {
            throw new BadRequestException(ChatResponse.CHAT_NOTICE_STATUS_IMMUTABLE);
        }
    }

    public void enterUser(User user, Chatroom chatroom) {
        ChatParticipant chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseGet(() -> ChatBuilder.buildChatParticipant(user, ChatroomRole.MEMBER, chatroom));

        chatParticipant.restoreParticipation();

        chatParticipantRepository.save(chatParticipant);
    }

    public ChatParticipant findByUsernameAndChatroom(String username, Chatroom chatroom) {
        return chatParticipantRepository.findByUsernameAndChatroom(username, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    public ChatParticipant findByUserAndChatroom(User user, Chatroom chatroom) {
        return chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    public Long countByChatroom(Chatroom chatroom) {
        return chatParticipantRepository.countByChatroomAndIsParticipatedTrue(chatroom);
    }

    public Boolean isJoined(User user, Chatroom chatroom) {
        return chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .map(ChatParticipant::getIsParticipated)
                .orElse(false);
    }

    public ChatParticipant getChatroomHost(Chatroom chatroom) {
        return chatParticipantRepository.getChatroomHost(chatroom);
    }

    public List<ChatParticipant> findAllByChatroom(Chatroom chatroom) {
        return chatParticipantRepository.findAllByChatroomAndIsParticipatedTrue(chatroom);
    }

    public boolean isAllParticipantLeft(Chatroom chatroom) {
        List<ChatParticipant> participants = chatParticipantRepository.findAllByChatroom(chatroom);
        return participants.stream()
                .noneMatch(ChatParticipant::getIsParticipated);
    }

    @Transactional
    public void deleteAllByChatroom(Chatroom chatroom) {
        chatParticipantRepository.deleteAllByChatroom(chatroom);
    }

    public List<ChatParticipantProfile> getParticipantProfiles(Chatroom chatroom) {
        List<ChatParticipant> participants = chatParticipantRepository.findAllByChatroomAndIsParticipatedTrue(chatroom);

        return participants.stream()
                .map(participant -> {
                    User user = participant.getUser();
                    return ChatParticipantProfile.builder()
                            .userId(user.getId())
                            .nickname(user.getNickname())
                            .profileImage(user.getProfileImage())
                            .rankingType(participant.getRankingStatus())
                            .isHost(ChatroomRole.HOST.equals(participant.getRole()))
                            .build();
                })
                .toList();
    }

    public List<ChatParticipant> findAllByChatroomExcludingUser(Chatroom chatroom, User excludedUser) {
        return chatParticipantRepository.findAllByChatroomAndIsParticipatedTrueAndUserNot(chatroom, excludedUser);
    }
}
