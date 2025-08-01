package com.poortorich.chat.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.ChatroomEnterRequest;
import com.poortorich.chat.request.ChatroomLeaveAllRequest;
import com.poortorich.chat.request.ChatroomUpdateRequest;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.response.ChatroomEnterResponse;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.response.ChatroomLeaveAllResponse;
import com.poortorich.chat.response.ChatroomLeaveResponse;
import com.poortorich.chat.response.ChatroomUpdateResponse;
import com.poortorich.chat.response.ChatroomResponse;
import com.poortorich.chat.response.ChatroomsResponse;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.chat.validator.ChatroomValidator;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.tag.service.TagService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatMessageService chatMessageService;
    private final FileUploadService fileUploadService;
    private final TagService tagService;

    private final ChatroomValidator chatroomValidator;
    private final ChatParticipantValidator chatParticipantValidator;

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

        return ChatroomCreateResponse.builder().newChatroomId(chatroom.getId()).build();
    }

    public ChatroomInfoResponse getChatroom(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        List<String> hashtags = tagService.getTagNames(chatroom);

        return ChatBuilder.buildChatroomInfoResponse(chatroom, hashtags);
    }

    public ChatroomsResponse getHostedChatrooms(String username) {
        User user = userService.findUserByUsername(username);
        List<Chatroom> hostedChatrooms = chatroomService.getHostedChatrooms(user);

        List<ChatroomResponse> chatroomResponses = hostedChatrooms.stream()
                .map(chatroom ->
                        ChatBuilder.buildChatroomResponse(
                                chatroom,
                                tagService.getTagNames(chatroom),
                                chatParticipantService.countByChatroom(chatroom),
                                chatMessageService.getLastMessageTime(chatroom)
                        ))
                .toList();

        return ChatroomsResponse.builder().chatrooms(chatroomResponses).build();
    }

    public ChatroomEnterResponse enterChatroom(
            String username,
            Long chatroomId,
            ChatroomEnterRequest chatroomEnterRequest
    ) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        User user = userService.findUserByUsername(username);

        chatroomValidator.validateEnter(user, chatroom);
        chatroomValidator.validatePassword(chatroom, chatroomEnterRequest.getChatroomPassword());

        chatParticipantService.enterUser(user, chatroom);
        return ChatroomEnterResponse.builder().chatroomId(chatroomId).build();
    }

    @Transactional
    public ChatroomUpdateResponse updateChatroom(
            String username,
            Long chatroomId,
            ChatroomUpdateRequest chatroomUpdateRequest
    ) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        User user = userService.findUserByUsername(username);
        String imageUrl = fileUploadService.uploadImage(chatroomUpdateRequest.getChatroomImage());

        chatroomValidator.validateCanUpdateMaxMemberCount(chatroom, chatroomUpdateRequest.getMaxMemberCount());
        chatParticipantValidator.validateIsHost(user, chatroom);

        chatroom.updateChatroom(chatroomUpdateRequest, imageUrl);
        tagService.updateTag(chatroomUpdateRequest.getHashtags(), chatroom);

        return ChatroomUpdateResponse.builder().chatroomId(chatroomId).build();
    }

    public ChatroomLeaveResponse leaveChatroom(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        chatroomValidator.validateParticipate(user, chatroom);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);
        chatParticipant.softDelete();

        return ChatroomLeaveResponse.builder().deleteChatroomId(chatroomId).build();
    }

    public ChatroomLeaveAllResponse leaveAllChatroom(String username, ChatroomLeaveAllRequest chatroomLeaveAllRequest) {
        User user = userService.findUserByUsername(username);
        for (Long chatroomId : chatroomLeaveAllRequest.getChatroomsToLeave()) {
            Chatroom chatroom = chatroomService.findById(chatroomId);

            chatroomValidator.validateParticipate(user, chatroom);
            ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);
            chatParticipant.softDelete();
        }

        return ChatroomLeaveAllResponse.builder()
                .deletedChatroomIds(chatroomLeaveAllRequest.getChatroomsToLeave())
                .build();
    }
}
