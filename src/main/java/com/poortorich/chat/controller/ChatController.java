package com.poortorich.chat.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.realtime.facade.ChatRealTimeFacade;
import com.poortorich.chat.realtime.payload.ResponsePayload;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.ChatroomEnterRequest;
import com.poortorich.chat.request.ChatroomLeaveAllRequest;
import com.poortorich.chat.request.ChatroomUpdateRequest;
import com.poortorich.chat.response.ChatroomEnterResponse;
import com.poortorich.chat.response.ChatroomLeaveAllResponse;
import com.poortorich.chat.response.ChatroomLeaveResponse;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatFacade chatFacade;
    private final ChatRealTimeFacade realTimeFacade;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> createChatroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid ChatroomCreateRequest request
    ) {
        return DataResponse.toResponseEntity(
                ChatResponse.CREATE_CHATROOM_SUCCESS,
                chatFacade.createChatroom(userDetails.getUsername(), request)
        );
    }

    @GetMapping("/{chatroomId}/edit")
    public ResponseEntity<BaseResponse> getChatroom(@PathVariable Long chatroomId) {
        return DataResponse.toResponseEntity(
                ChatResponse.GET_CHATROOM_SUCCESS,
                chatFacade.getChatroom(chatroomId)
        );
    }

    @PostMapping("/{chatroomId}/enter")
    public ResponseEntity<BaseResponse> enterChatroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("chatroomId") Long chatroomId,
            @RequestBody ChatroomEnterRequest chatroomEnterRequest
    ) {
        ChatroomEnterResponse response = chatFacade.enterChatroom(
                userDetails.getUsername(),
                chatroomId,
                chatroomEnterRequest);

        ResponsePayload payload = realTimeFacade.createUserEnterSystemMessage(userDetails.getUsername(), chatroomId);
        messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId, payload);

        return DataResponse.toResponseEntity(
                ChatResponse.CHATROOM_ENTER_SUCCESS,
                response
        );
    }

    @PutMapping(value = "/{chatroomId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> updateChatroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("chatroomId") Long chatroomId,
            @Valid ChatroomUpdateRequest chatroomUpdateRequest
    ) {
        return DataResponse.toResponseEntity(
                ChatResponse.CHATROOM_UPDATE_SUCCESS,
                chatFacade.updateChatroom(userDetails.getUsername(), chatroomId, chatroomUpdateRequest));
    }

    @DeleteMapping("/{chatroomId}")
    public ResponseEntity<BaseResponse> leaveChatroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("chatroomId") Long chatroomId
    ) {
        ChatroomLeaveResponse response = chatFacade.leaveChatroom(userDetails.getUsername(), chatroomId);
        ResponsePayload payload = realTimeFacade.createUserLeaveSystemMessage(userDetails.getUsername(), chatroomId);

        messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId, payload);

        return DataResponse.toResponseEntity(
                ChatResponse.CHATROOM_LEAVE_SUCCESS,
                response
        );
    }

    @DeleteMapping("/leave")
    public ResponseEntity<BaseResponse> leaveAllChatroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatroomLeaveAllRequest chatroomLeaveAllRequest
    ) {
        ChatroomLeaveAllResponse response = chatFacade.leaveAllChatroom(
                userDetails.getUsername(),
                chatroomLeaveAllRequest);

        for (Long chatroomId : chatroomLeaveAllRequest.getChatroomsToLeave()) {
            ResponsePayload payload = realTimeFacade.createUserLeaveSystemMessage(
                    userDetails.getUsername(),
                    chatroomId);

            messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId, payload);
        }

        return DataResponse.toResponseEntity(
                ChatResponse.CHATROOM_LEAVE_SUCCESS,
                response
        );
    }
}
