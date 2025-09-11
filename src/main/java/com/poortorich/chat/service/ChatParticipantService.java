package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.model.ChatroomPaginationContext;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.chat.response.ChatParticipantProfile;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chat.util.mapper.ParticipantProfileMapper;
import com.poortorich.chatnotice.request.ChatNoticeStatusUpdateRequest;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import com.poortorich.websocket.stomp.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final SubscribeService subscribeService;

    private final ChatBuilder chatBuilder;
    private final ParticipantProfileMapper profileMapper;

    public void createChatroomHost(User user, Chatroom chatroom) {
        ChatParticipant chatParticipant = chatBuilder.buildChatParticipant(user, ChatroomRole.HOST, chatroom);
        chatParticipantRepository.save(chatParticipant);
    }

    public void updateNoticeStatus(String username, Chatroom chatroom, ChatNoticeStatusUpdateRequest request) {
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

    public ChatParticipant enterUser(User user, Chatroom chatroom) {
        ChatParticipant chatParticipant = chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseGet(() -> chatBuilder.buildChatParticipant(user, ChatroomRole.MEMBER, chatroom));

        chatParticipant.restoreParticipation();

        return chatParticipantRepository.save(chatParticipant);
    }

    public ChatParticipant findByUsernameAndChatroom(String username, Chatroom chatroom) {
        return chatParticipantRepository.findByUsernameAndChatroom(username, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    public ChatParticipant findByUserAndChatroom(User user, Chatroom chatroom) {
        return chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    public Optional<ChatParticipant> getChatParticipant(User user, Chatroom chatroom) {
        return chatParticipantRepository.findByUserAndChatroom(user, chatroom);
    }

    public ChatParticipant findByUserIdAndChatroom(Long userId, Chatroom chatroom) {
        return chatParticipantRepository.findByUserIdAndChatroom(userId, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    public Long countByChatroom(Chatroom chatroom) {
        return chatParticipantRepository.countParticipantsByChatroom(chatroom);
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
        chatParticipantRepository.deleteByChatroom(chatroom);
    }

    public List<ChatParticipantProfile> getParticipantProfiles(Chatroom chatroom) {
        List<ChatParticipant> participants = chatParticipantRepository.findAllByChatroom(chatroom);

        return participants.stream()
                .map(profileMapper::mapToProfile)
                .toList();
    }

    public List<ChatParticipant> findAllByChatroomExcludingUser(Chatroom chatroom, User excludedUser) {
        return chatParticipantRepository.findAllByChatroomAndIsParticipatedTrueAndUserNot(chatroom, excludedUser);
    }

    public List<ChatParticipant> findAllByUser(User user) {
        return chatParticipantRepository.findAllByUserAndIsParticipatedTrue(user);
    }

    public List<ChatParticipant> findAllByUsernameWithChatroomAndUser(String username) {
        return chatParticipantRepository.findAllByUsernameWithChatroomAndUser(username);
    }

    @Transactional
    public List<ChatParticipant> findUnreadMembers(Chatroom chatroom, User user) {
        Set<String> activeSubscribers = subscribeService.getSubscribers(chatroom.getId());

        return chatParticipantRepository.findAllByChatroomAndIsParticipatedTrueAndUserNot(chatroom, user).stream()
                .filter(chatParticipant -> !activeSubscribers.contains(chatParticipant.getUser().getUsername()))
                .filter(chatParticipant -> !ChatroomRole.BANNED.equals(chatParticipant.getRole()))
                .toList();
    }

    public void updateAllNoticeStatus(List<ChatParticipant> chatParticipants, NoticeStatus noticeStatus) {
        chatParticipants.forEach(chatParticipant -> chatParticipant.updateNoticeStatus(noticeStatus));
    }

    public List<ChatParticipant> getAllParticipants(Chatroom chatroom) {
        return chatParticipantRepository.findAllOrderedParticipants(chatroom);
    }

    public ChatParticipant findByUsernameAndChatroomId(String username, Long chatroomId) {
        return chatParticipantRepository.findByUsernameAndChatroomId(username, chatroomId)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    public ChatParticipant findByUserIdAndChatroomId(Long userId, Long chatroomId) {
        return chatParticipantRepository.findByUserIdAndChatroomId(userId, chatroomId)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    @Transactional
    public void delegateHost(ChatParticipant currentHost, ChatParticipant nextHost) {
        currentHost.updateChatroomRole(ChatroomRole.MEMBER);
        nextHost.updateChatroomRole(ChatroomRole.HOST);
    }

    public List<ChatParticipant> searchParticipantsByNickname(Chatroom chatroom, String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return chatParticipantRepository.findAllOrderedParticipants(chatroom);
        }

        return chatParticipantRepository.searchByChatroomAndNickname(chatroom, nickname);
    }

    @Transactional
    public void kickChatParticipant(ChatParticipant kickChatParticipant) {
        kickChatParticipant.kick();
    }

    public Slice<ChatParticipant> getMyParticipants(ChatroomPaginationContext context) {
        return chatParticipantRepository
                .findMyParticipants(
                        context.user(),
                        context.cursor(),
                        context.pageRequest());
    }

    @Transactional
    public void updateAllRankingStatus(List<ChatParticipant> participants, RankingStatus rankingStatus) {
        participants.forEach(participant -> participant.updateRankingStatus(rankingStatus));
    }

    public ChatParticipant findByChatroomAndRankingStatus(Chatroom chatroom, RankingStatus rankingStatus) {
        return chatParticipantRepository.findByChatroomAndRankingStatus(chatroom, rankingStatus)
                .orElse(null);
    }

    public ChatParticipant findByIdOrThrow(Long chatParticipantId) {
        return chatParticipantRepository.findById(chatParticipantId)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND));
    }

    @Transactional
    public void updateEnterMessageId(User user, Chatroom chatroom, Long messageId) {
        chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND))
                .updateEnterMessageId(messageId);
    }

    @Transactional
    public void updateKickMessageId(User user, Chatroom chatroom, Long messageId) {
        chatParticipantRepository.findByUserAndChatroom(user, chatroom)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND))
                .updateKickMessageId(messageId);
    }
}
