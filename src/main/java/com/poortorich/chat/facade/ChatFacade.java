package com.poortorich.chat.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.ChatroomEnterRequest;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.response.ChatroomEnterResponse;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.validator.ChatroomValidator;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.tag.service.TagService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final FileUploadService fileUploadService;
    private final TagService tagService;

    private final ChatroomValidator chatroomValidator;

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

    public ChatroomEnterResponse enterChatroom(
            String username,
            Long chatroomId,
            ChatroomEnterRequest chatroomEnterRequest
    ) {
        Chatroom chatroom = chatroomService.findByChatroomId(chatroomId);
        User user = userService.findUserByUsername(username);

        chatroomValidator.validateEnter(user, chatroom);
        chatroomValidator.validatePassword(chatroom, chatroomEnterRequest.getChatroomPassword());

        chatParticipantService.enterUser(user, chatroom);

        return ChatroomEnterResponse.builder().chatroomId(chatroomId).build();
    }
}
