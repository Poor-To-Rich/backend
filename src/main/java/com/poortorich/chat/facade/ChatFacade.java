package com.poortorich.chat.facade;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.model.ChatPaginationContext;
import com.poortorich.chat.model.UserEnterChatroomResult;
import com.poortorich.chat.realtime.payload.response.UserEnterProfileResponsePayload;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.ChatroomEnterRequest;
import com.poortorich.chat.request.ChatroomLeaveAllRequest;
import com.poortorich.chat.request.ChatroomUpdateRequest;
import com.poortorich.chat.request.enums.SortBy;
import com.poortorich.chat.response.AllChatroomsResponse;
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
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chat.util.detector.RankingStatusChangeDetector;
import com.poortorich.chat.util.mapper.ChatMessageMapper;
import com.poortorich.chat.util.provider.ChatPaginationProvider;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.chat.validator.ChatroomValidator;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.tag.service.TagService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatMessageService chatMessageService;
    private final FileUploadService fileUploadService;
    private final TagService tagService;

    private final ChatMessageMapper chatMessageMapper;
    private final ChatPaginationProvider paginationProvider;
    private final ChatroomValidator chatroomValidator;
    private final ChatParticipantValidator chatParticipantValidator;
    private final RankingStatusChangeDetector rankingStatusChangeDetector;

    @Transactional
    public ChatroomCreateResponse createChatroom(
            String username,
            ChatroomCreateRequest request
    ) {
        User user = userService.findUserByUsername(username);
        String imageUrl = fileUploadService.uploadImage(request.getChatroomImage());

        Chatroom chatroom = chatroomService.createChatroom(imageUrl, request);
        chatParticipantService.createChatroomHost(user, chatroom);
        if (request.getHashtags() != null) {
            tagService.createTag(request.getHashtags(), chatroom);
        }

        return ChatroomCreateResponse.builder().newChatroomId(chatroom.getId()).build();
    }

    public ChatroomInfoResponse getChatroom(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        List<String> hashtags = tagService.getTagNames(chatroom);

        return ChatBuilder.buildChatroomInfoResponse(chatroom, hashtags);
    }

    public AllChatroomsResponse getAllChatrooms(SortBy sortBy, Long cursor) {
        List<Chatroom> chatrooms = chatroomService.getAllChatrooms(sortBy, cursor);

        return AllChatroomsResponse.builder()
                .hasNext(chatroomService.hasNext(sortBy, chatrooms.getLast().getId()))
                .nextCursor(chatroomService.getNextCursor(sortBy, chatrooms.getLast().getId()))
                .chatrooms(getChatroomResponses(chatrooms))
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
                .map(chatroom ->
                        ChatBuilder.buildChatroomResponse(
                                chatroom,
                                tagService.getTagNames(chatroom),
                                chatParticipantService.countByChatroom(chatroom),
                                chatMessageService.getLastMessageTime(chatroom)
                        ))
                .toList();
    }

    public ChatroomDetailsResponse getChatroomDetails(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        return ChatBuilder.buildChatroomDetailsResponse(chatroom, chatParticipantService.countByChatroom(chatroom));
    }

    public ChatroomCoverInfoResponse getChatroomCoverInfo(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        return ChatBuilder.buildChatroomCoverInfoResponse(
                chatroom,
                tagService.getTagNames(chatroom),
                chatParticipantService.countByChatroom(chatroom),
                chatParticipantService.isJoined(user, chatroom),
                chatParticipantService.getChatroomHost(chatroom)
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

    public void updateNoticeStatus(String username, Long chatroomId, ChatNoticeUpdateRequest request) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantService.updateNoticeStatus(username, chatroom, request);
    }

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

        chatroom.updateChatroom(chatroomUpdateRequest, newChatroomImage);
        tagService.updateTag(chatroomUpdateRequest.getHashtags(), chatroom);

        return ChatroomUpdateResponse.builder()
                .chatroomId(chatroomId)
                .isChangedRankingStatus(isChangedRankingStatus)
                .build();
    }

    public ChatroomLeaveResponse leaveChatroom(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        chatroomValidator.validateParticipate(user, chatroom);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);
        if (chatParticipant.getRole().equals(ChatroomRole.HOST)) {
            chatParticipant.softDelete();
            deleteChatroom(chatroom);
        } else {
            chatParticipant.softDelete();
        }

        return ChatroomLeaveResponse.builder().deleteChatroomId(chatroomId).build();
    }

    @Transactional
    public ChatroomLeaveAllResponse leaveAllChatroom(String username, ChatroomLeaveAllRequest chatroomLeaveAllRequest) {
        User user = userService.findUserByUsername(username);
        for (Long chatroomId : chatroomLeaveAllRequest.getChatroomsToLeave()) {
            Chatroom chatroom = chatroomService.findById(chatroomId);

            chatroomValidator.validateParticipate(user, chatroom);
            ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);
            if (chatParticipant.getRole().equals(ChatroomRole.HOST)) {
                chatParticipant.softDelete();
                deleteChatroom(chatroom);
            } else {
                chatParticipant.softDelete();
            }
        }

        return ChatroomLeaveAllResponse.builder()
                .deletedChatroomIds(chatroomLeaveAllRequest.getChatroomsToLeave())
                .build();
    }

    @Transactional
    private void deleteChatroom(Chatroom chatroom) {
        tagService.deleteAllByChatroom(chatroom);
        chatMessageService.closeAllMessagesByChatroom(chatroom);
        chatroomService.closeChatroomById(chatroom.getId());
    }

    public ChatMessagePageResponse getChatMessages(String username, Long chatroomId, Long cursor, Long pageSize) {
        User user = userService.findUserByUsername(username);
        ChatPaginationContext context = paginationProvider.getContext(chatroomId, cursor, pageSize);
        chatParticipantValidator.validateIsParticipate(user, context.chatroom());

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
}
