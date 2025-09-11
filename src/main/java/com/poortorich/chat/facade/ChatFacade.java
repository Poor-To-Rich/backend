package com.poortorich.chat.facade;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.model.ChatPaginationContext;
import com.poortorich.chat.model.ChatroomPaginationContext;
import com.poortorich.chat.model.UserEnterChatroomResult;
import com.poortorich.chat.realtime.event.chatparticipants.KickChatroomEvent;
import com.poortorich.chat.realtime.event.chatroom.ChatroomUpdateEvent;
import com.poortorich.chat.realtime.event.chatroom.ParticipantUpdateEvent;
import com.poortorich.chat.realtime.event.chatroom.detector.ChatroomUpdateDetector;
import com.poortorich.chat.realtime.payload.response.UserEnterProfileResponsePayload;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.ChatroomEnterRequest;
import com.poortorich.chat.request.ChatroomLeaveAllRequest;
import com.poortorich.chat.request.ChatroomUpdateRequest;
import com.poortorich.chat.request.HostDelegationRequest;
import com.poortorich.chat.request.enums.SortBy;
import com.poortorich.chat.response.AllChatroomsResponse;
import com.poortorich.chat.response.AllParticipantsResponse;
import com.poortorich.chat.response.ChatMessagePageResponse;
import com.poortorich.chat.response.ChatParticipantProfile;
import com.poortorich.chat.response.ChatroomCoverInfoResponse;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.response.ChatroomDetailsResponse;
import com.poortorich.chat.response.ChatroomEnterResponse;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.response.ChatroomLeaveAllResponse;
import com.poortorich.chat.response.ChatroomLeaveResponse;
import com.poortorich.chat.response.ChatroomResponse;
import com.poortorich.chat.response.ChatroomRoleResponse;
import com.poortorich.chat.response.ChatroomUpdateResponse;
import com.poortorich.chat.response.ChatroomsResponse;
import com.poortorich.chat.response.HostDelegationResponse;
import com.poortorich.chat.response.KickChatParticipantResponse;
import com.poortorich.chat.response.MyChatroom;
import com.poortorich.chat.response.MyChatroomsResponse;
import com.poortorich.chat.response.SearchParticipantsResponse;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomLeaveService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chat.util.detector.RankingStatusChangeDetector;
import com.poortorich.chat.util.mapper.ChatMessageMapper;
import com.poortorich.chat.util.mapper.ChatroomMapper;
import com.poortorich.chat.util.mapper.ParticipantProfileMapper;
import com.poortorich.chat.util.provider.ChatPaginationProvider;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.chat.validator.ChatroomValidator;
import com.poortorich.chatnotice.request.ChatNoticeStatusUpdateRequest;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.tag.service.TagService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatroomLeaveService chatroomLeaveService;
    private final ChatMessageService chatMessageService;
    private final FileUploadService fileUploadService;
    private final TagService tagService;

    private final ChatMessageMapper chatMessageMapper;
    private final ChatroomMapper chatroomMapper;
    private final ChatPaginationProvider paginationProvider;
    private final ChatroomValidator chatroomValidator;
    private final ChatParticipantValidator chatParticipantValidator;
    private final RankingStatusChangeDetector rankingStatusChangeDetector;
    private final ChatroomUpdateDetector chatroomUpdateDetector;
    private final ParticipantProfileMapper participantProfileMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatBuilder chatBuilder;

    @Transactional
    public ChatroomCreateResponse createChatroom(
            String username,
            ChatroomCreateRequest request
    ) {
        User user = userService.findUserByUsername(username);
        String imageUrl = fileUploadService.uploadImage(request.getChatroomImage());

        Chatroom chatroom = chatroomService.createChatroom(imageUrl, request);
        chatParticipantService.createChatroomHost(user, chatroom);
        tagService.createTag(request.getHashtags(), chatroom);

        chatroomService.overwriteChatroomsInRedis();

        return ChatroomCreateResponse.builder().newChatroomId(chatroom.getId()).build();
    }

    public ChatroomInfoResponse getChatroom(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        List<String> hashtags = tagService.getTagNames(chatroom);

        return chatBuilder.buildChatroomInfoResponse(chatroom, hashtags);
    }

    public AllChatroomsResponse getAllChatrooms(SortBy sortBy, Long cursor) {
        if (sortBy.equals(SortBy.CREATED_AT)) {
            return getAllChatroomsSortByCreatedAt(cursor);
        }

        List<Chatroom> chatrooms = chatroomService.getAllChatrooms(sortBy, cursor);

        if (chatrooms.isEmpty()) {
            return getAllChatroomsResponseEmptyChatroom();
        }

        return AllChatroomsResponse.builder()
                .hasNext(chatroomService.hasNext(sortBy, chatrooms.getLast().getId()))
                .nextCursor(chatroomService.getNextCursor(sortBy, chatrooms.getLast().getId()))
                .chatrooms(getChatroomResponses(chatrooms))
                .build();
    }

    private AllChatroomsResponse getAllChatroomsSortByCreatedAt(Long cursor) {
        if (cursor == -1) {
            cursor = Long.MAX_VALUE;
        }
        Pageable pageable = PageRequest.of(0, 20);
        Slice<Chatroom> chatroomSlice = chatroomService.findByCursorSortByCreatedAt(cursor, pageable);
        List<Chatroom> chatrooms = chatroomSlice.getContent();

        if (chatrooms.isEmpty()) {
            return getAllChatroomsResponseEmptyChatroom();
        }

        return AllChatroomsResponse.builder()
                .hasNext(chatroomSlice.hasNext())
                .nextCursor(chatroomSlice.hasNext() ? chatrooms.getLast().getId() : null)
                .chatrooms(getChatroomResponses(chatrooms))
                .build();
    }

    private AllChatroomsResponse getAllChatroomsResponseEmptyChatroom() {
        return AllChatroomsResponse.builder()
                .hasNext(false)
                .nextCursor(null)
                .chatrooms(List.of())
                .build();
    }

    public ChatroomsResponse searchChatrooms(String keyword) {
        List<Chatroom> chatrooms = chatroomService.searchChatrooms(keyword);

        return ChatroomsResponse.builder()
                .chatrooms(getChatroomResponses(chatrooms))
                .build();
    }

    public ChatroomsResponse getHostedChatrooms(String username) {
        User user = userService.findUserByUsername(username);
        List<Chatroom> hostedChatrooms = chatroomService.getHostedChatrooms(user);

        return ChatroomsResponse.builder()
                .chatrooms(getChatroomResponses(hostedChatrooms))
                .build();
    }

    private List<ChatroomResponse> getChatroomResponses(List<Chatroom> chatrooms) {
        return chatrooms.stream()
                .filter(Objects::nonNull)
                .map(chatroom ->
                        chatBuilder.buildChatroomResponse(
                                chatroom,
                                tagService.getTagNames(chatroom),
                                chatParticipantService.countByChatroom(chatroom),
                                chatMessageService.getLastMessageTime(chatroom)
                        ))
                .toList();
    }

    public ChatroomDetailsResponse getChatroomDetails(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        return chatBuilder.buildChatroomDetailsResponse(chatroom, chatParticipantService.countByChatroom(chatroom));
    }

    public ChatroomCoverInfoResponse getChatroomCoverInfo(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        ChatParticipant participant = chatParticipantService.getChatParticipant(user, chatroom)
                .orElse(null);

        return chatBuilder.buildChatroomCoverInfoResponse(
                chatroom,
                tagService.getTagNames(chatroom),
                chatParticipantService.countByChatroom(chatroom),
                chatParticipantService.isJoined(user, chatroom),
                chatParticipantService.getChatroomHost(chatroom),
                participant != null && participant.getIsParticipated() ? chatMessageService.getLatestReadMessageId(participant) : null
        );
    }

    public ChatroomRoleResponse getChatroomRole(String username, Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUsernameAndChatroom(username, chatroom);

        return ChatroomRoleResponse.builder()
                .userId(chatParticipant.getUser().getId())
                .chatroomRole(chatParticipant.getRole().toString())
                .build();
    }

    public void updateNoticeStatus(String username, Long chatroomId, ChatNoticeStatusUpdateRequest request) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantService.updateNoticeStatus(username, chatroom, request);
    }

    @Transactional
    public UserEnterChatroomResult enterChatroom(
            String username,
            Long chatroomId,
            ChatroomEnterRequest chatroomEnterRequest
    ) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        User user = userService.findUserByUsername(username);

        chatroomValidator.validateEnter(user, chatroom);
        chatroomValidator.validatePassword(chatroom, chatroomEnterRequest.getChatroomPassword());

        ChatParticipant newParticipant = chatParticipantService.enterUser(user, chatroom);

        eventPublisher.publishEvent(new ChatroomUpdateEvent(chatroom, PayloadType.CHATROOM_INFO_UPDATED));
        return UserEnterChatroomResult.builder()
                .apiResponse(ChatroomEnterResponse.builder().chatroomId(chatroomId).build())
                .broadcastPayload(UserEnterProfileResponsePayload.builder()
                        .userId(user.getId())
                        .profileImage(user.getProfileImage())
                        .nickname(user.getNickname())
                        .isHost(ChatroomRole.HOST.equals(newParticipant.getRole()))
                        .rankingType(newParticipant.getRankingStatus())
                        .build())
                .build();
    }

    @Transactional
    public ChatroomUpdateResponse updateChatroom(
            String username,
            Long chatroomId,
            ChatroomUpdateRequest chatroomUpdateRequest
    ) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        User user = userService.findUserByUsername(username);

        chatParticipantValidator.validateIsHost(user, chatroom);
        chatroomValidator.validateCanUpdateMaxMemberCount(chatroom, chatroomUpdateRequest.getMaxMemberCount());

        String newChatroomImage = fileUploadService.updateImage(
                chatroom.getImage(),
                chatroomUpdateRequest.getChatroomImage(),
                chatroomUpdateRequest.getIsDefaultProfile());

        Boolean isChangedRankingStatus = rankingStatusChangeDetector.detectRankingChange(
                chatroom.getIsRankingEnabled(),
                chatroomUpdateRequest.getIsRankingEnabled());

        chatroomUpdateDetector.detect(chatroom, chatroomUpdateRequest, newChatroomImage);

        chatroom.updateChatroom(chatroomUpdateRequest, newChatroomImage);
        tagService.updateTag(chatroomUpdateRequest.getHashtags(), chatroom);

        return ChatroomUpdateResponse.builder()
                .chatroomId(chatroomId)
                .isChangedRankingStatus(isChangedRankingStatus)
                .build();
    }

    @Transactional
    public ChatroomLeaveResponse leaveChatroom(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        chatroomLeaveService.leaveChatroom(chatParticipant);

        return ChatroomLeaveResponse.builder().deleteChatroomId(chatroomId).build();
    }

    @Transactional
    public ChatroomLeaveAllResponse leaveAllChatroom(String username, ChatroomLeaveAllRequest chatroomLeaveAllRequest) {
        User user = userService.findUserByUsername(username);
        for (Long chatroomId : chatroomLeaveAllRequest.getChatroomsToLeave()) {
            Chatroom chatroom = chatroomService.findById(chatroomId);
            ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);
            chatroomLeaveService.leaveChatroom(chatParticipant);

        }

        return ChatroomLeaveAllResponse.builder()
                .deletedChatroomIds(chatroomLeaveAllRequest.getChatroomsToLeave())
                .build();
    }

    @Transactional
    public void deleteChatroom(Chatroom chatroom) {
        tagService.deleteAllByChatroom(chatroom);
        chatMessageService.closeAllMessagesByChatroom(chatroom);
        chatroomService.closeChatroomById(chatroom.getId());
    }

    @Transactional
    public ChatMessagePageResponse getChatMessages(String username, Long chatroomId, Long cursor, Long pageSize) {
        ChatPaginationContext context = paginationProvider.getChatMessagesContext(username, chatroomId, cursor, pageSize);
        chatParticipantValidator.validateIsParticipate(context.chatParticipant().getUser(), context.chatroom());

        Slice<ChatMessage> chatMessages = chatMessageService.getChatMessages(context);

        Long nextCursor = paginationProvider.getNextCursor(chatMessages);
        List<ChatMessageResponse> messages = chatMessages.getContent().stream()
                .map(chatMessageMapper::mapToChatMessageResponse)
                .toList();

        return ChatMessagePageResponse.builder()
                .nextCursor(nextCursor)
                .hasNext(chatMessages.hasNext())
                .messages(messages)
                .users(chatParticipantService.getParticipantProfiles(context.chatroom())
                        .stream()
                        .collect(Collectors.toMap(
                                ChatParticipantProfile::getUserId,
                                profile -> profile
                        )))
                .build();
    }

    @Transactional
    public MyChatroomsResponse getMyChatrooms(String username, Long cursor) {
        ChatroomPaginationContext context = paginationProvider.getMyChatroomsContext(username, cursor);

        Slice<ChatParticipant> participants = chatParticipantService.getMyParticipants(context);

        Long nextCursor = paginationProvider.getChatroomNextCursor(participants);

        List<MyChatroom> myChatrooms = participants.stream()
                .map(chatroomMapper::mapToMyChatroom)
                .sorted()
                .toList();

        return MyChatroomsResponse.builder()
                .nextCursor(nextCursor)
                .hasNext(participants.hasNext())
                .chatrooms(myChatrooms)
                .build();
    }

    @Transactional(readOnly = true)
    public AllParticipantsResponse getAllParticipants(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantValidator.validateIsParticipate(user, chatroom);

        return chatBuilder.buildAllParticipantsResponse(chatParticipantService.getAllParticipants(chatroom));
    }

    @Transactional
    public HostDelegationResponse delegateHost(String username, Long chatroomId, HostDelegationRequest request) {
        ChatParticipant currentHost = chatParticipantService.findByUsernameAndChatroomId(username, chatroomId);
        ChatParticipant nextHost = chatParticipantService.findByUserIdAndChatroomId(
                request.getTargetUserId(),
                chatroomId);

        chatParticipantValidator.validateIsHost(currentHost);
        chatParticipantValidator.validateIsParticipate(nextHost);
        chatParticipantValidator.validateIsMember(nextHost);

        chatParticipantService.delegateHost(currentHost, nextHost);

        eventPublisher.publishEvent(new ParticipantUpdateEvent(nextHost));
        eventPublisher.publishEvent(new ParticipantUpdateEvent(currentHost));

        return HostDelegationResponse.builder()
                .newHostUserId(nextHost.getUser().getId())
                .prevHost(currentHost)
                .newHost(nextHost)
                .build();
    }

    @Transactional(readOnly = true)
    public SearchParticipantsResponse searchParticipantsByNickname(String username, Long chatroomId, String nickname) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantValidator.validateIsHost(user, chatroom);

        List<ChatParticipant> chatParticipants = chatParticipantService.searchParticipantsByNickname(chatroom, nickname);

        return SearchParticipantsResponse.builder()
                .members(chatParticipants.stream()
                        .filter(chatParticipant -> chatParticipant.getRole().equals(ChatroomRole.MEMBER))
                        .map(participantProfileMapper::mapToProfile)
                        .toList())
                .build();
    }

    @Transactional
    public KickChatParticipantResponse kickChatParticipant(String username, Long chatroomId, Long userId) {
        ChatParticipant host = chatParticipantService.findByUsernameAndChatroomId(username, chatroomId);
        ChatParticipant kickChatParticipant = chatParticipantService.findByUserIdAndChatroomId(userId, chatroomId);

        chatParticipantValidator.validateIsHost(host);
        chatParticipantValidator.validateIsMember(kickChatParticipant);
        chatParticipantValidator.validateIsParticipate(kickChatParticipant);

        eventPublisher.publishEvent(new KickChatroomEvent(kickChatParticipant.getId()));
        chatParticipantService.kickChatParticipant(kickChatParticipant);

        eventPublisher.publishEvent(new ChatroomUpdateEvent(host.getChatroom(), PayloadType.CHATROOM_INFO_UPDATED));
        return KickChatParticipantResponse.builder()
                .kickUserId(kickChatParticipant.getUser().getId())
                .kickChatParticipant(kickChatParticipant)
                .build();
    }
}
