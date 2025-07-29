package com.poortorich.chat.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.realtime.facade.ChatRealTimeFacade;
import com.poortorich.chat.realtime.payload.ResponsePayload;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.ChatroomEnterRequest;
import com.poortorich.chat.response.ChatroomEnterResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

        ResponsePayload payload = realTimeFacade.createUserEnterSystemMessage(
                userDetails.getUsername(),
                chatroomId);

        messagingTemplate.convertAndSend(
                SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                payload);

        return DataResponse.toResponseEntity(
                ChatResponse.CHATROOM_ENTER_SUCCESS,
                response
        );
    }
}
