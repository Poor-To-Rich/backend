package com.poortorich.chat.controller;

import com.poortorich.chat.constants.ChatResponseMessage;
import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.model.MarkAllChatroomAsReadResult;
import com.poortorich.chat.model.UserEnterChatroomResult;
import com.poortorich.chat.realtime.facade.ChatRealTimeFacade;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.ChatroomEnterRequest;
import com.poortorich.chat.request.ChatroomLeaveAllRequest;
import com.poortorich.chat.request.ChatroomUpdateRequest;
import com.poortorich.chat.request.HostDelegationRequest;
import com.poortorich.chat.request.enums.SortBy;
import com.poortorich.chat.response.ChatMessagePageResponse;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.response.ChatroomLeaveAllResponse;
import com.poortorich.chat.response.ChatroomLeaveResponse;
import com.poortorich.chat.response.ChatroomUpdateResponse;
import com.poortorich.chat.response.HostDelegationResponse;
import com.poortorich.chat.response.KickChatParticipantResponse;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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
        ChatroomCreateResponse response = chatFacade.createChatroom(userDetails.getUsername(), request);
        realTimeFacade.createDateChangeSystemMessage(response.getNewChatroomId());
        realTimeFacade.createUserEnterSystemMessage(userDetails.getUsername(), response.getNewChatroomId());
        realTimeFacade.createRankingStatusMessage(response.getNewChatroomId(), request.getIsRankingEnabled());
        return DataResponse.toResponseEntity(ChatResponse.CREATE_CHATROOM_SUCCESS, response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getAllChatrooms(
            @RequestParam(defaultValue = "UPDATED_AT") SortBy sortBy,
            @RequestParam(defaultValue = "-1") Long cursor
    ) {
        String message = sortBy.getMessage() + ChatResponseMessage.GET_ALL_CHATROOMS_SUCCESS;

        return DataResponse.toResponseEntity(HttpStatus.OK, message, chatFacade.getAllChatrooms(sortBy, cursor));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse> searchChatrooms(@RequestParam String keyword) {
        return DataResponse.toResponseEntity(
                ChatResponse.GET_SEARCH_CHATROOMS_SUCCESS,
                chatFacade.searchChatrooms(keyword.trim())
        );
    }

    @GetMapping("/{chatroomId}")
    public ResponseEntity<BaseResponse> getChatroomCoverInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId
    ) {
        return DataResponse.toResponseEntity(
                ChatResponse.GET_CHATROOM_COVER_INFO_SUCCESS,
                chatFacade.getChatroomCoverInfo(userDetails.getUsername(), chatroomId)
        );
    }

    @GetMapping("/{chatroomId}/edit")
    public ResponseEntity<BaseResponse> getChatroom(@PathVariable Long chatroomId) {
        return DataResponse.toResponseEntity(ChatResponse.GET_CHATROOM_SUCCESS, chatFacade.getChatroom(chatroomId));
    }

    @GetMapping("/{chatroomId}/details")
    public ResponseEntity<BaseResponse> getChatroomDetails(@PathVariable Long chatroomId) {
        return DataResponse.toResponseEntity(
                ChatResponse.GET_CHATROOM_DETAILS_SUCCESS,
                chatFacade.getChatroomDetails(chatroomId)
        );
    }

    @GetMapping("/{chatroomId}/role")
    public ResponseEntity<BaseResponse> getChatroomRole(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId
    ) {
        return DataResponse.toResponseEntity(
                ChatResponse.GET_CHATROOM_ROLE_SUCCESS,
                chatFacade.getChatroomRole(userDetails.getUsername(), chatroomId)
        );
    }

    @PostMapping("/{chatroomId}/enter")
    public ResponseEntity<BaseResponse> enterChatroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("chatroomId") Long chatroomId,
            @RequestBody ChatroomEnterRequest chatroomEnterRequest
    ) {
        UserEnterChatroomResult result = chatFacade.enterChatroom(
                userDetails.getUsername(),
                chatroomId,
                chatroomEnterRequest);

        BasePayload basePayload = realTimeFacade.createUserEnterSystemMessage(userDetails.getUsername(),
                chatroomId);

        if (!Objects.isNull(basePayload)) {
            messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId, basePayload);
        }

        if (!Objects.isNull(result.getBroadcastPayload())) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                    result.getBroadcastPayload().mapToBasePayload());
        }

        return DataResponse.toResponseEntity(ChatResponse.CHATROOM_ENTER_SUCCESS, result.getApiResponse());
    }

    @PutMapping(value = "/{chatroomId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> updateChatroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("chatroomId") Long chatroomId,
            @Valid ChatroomUpdateRequest chatroomUpdateRequest
    ) {
        ChatroomUpdateResponse response = chatFacade.updateChatroom(
                userDetails.getUsername(),
                chatroomId,
                chatroomUpdateRequest);

        BasePayload payload = realTimeFacade.createRankingStatusMessage(
                chatroomId,
                response.getIsChangedRankingStatus());

        if (!Objects.isNull(payload)) {
            messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId, payload);
        }

        return DataResponse.toResponseEntity(ChatResponse.CHATROOM_UPDATE_SUCCESS, response);
    }

    @DeleteMapping("/{chatroomId}")
    public ResponseEntity<BaseResponse> leaveChatroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("chatroomId") Long chatroomId
    ) {
        ChatroomLeaveResponse response = chatFacade.leaveChatroom(userDetails.getUsername(), chatroomId);

        BasePayload leavePayload = realTimeFacade.createUserLeaveSystemMessage(userDetails.getUsername(), chatroomId);
        BasePayload closedPayload = realTimeFacade.createChatroomClosedMessageOrDeleteAll(
                userDetails.getUsername(),
                chatroomId);

        if (!Objects.isNull(leavePayload)) {
            messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId, leavePayload);
        }
        if (!Objects.isNull(closedPayload)) {
            messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId, closedPayload);
        }
        return DataResponse.toResponseEntity(ChatResponse.CHATROOM_LEAVE_SUCCESS, response);
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
            BasePayload leavePayload = realTimeFacade.createUserLeaveSystemMessage(
                    userDetails.getUsername(),
                    chatroomId);
            BasePayload closedPayload = realTimeFacade.createChatroomClosedMessageOrDeleteAll(
                    userDetails.getUsername(),
                    chatroomId);

            if (!Objects.isNull(leavePayload)) {
                messagingTemplate.convertAndSend(
                        SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                        leavePayload);
            }

            if (!Objects.isNull(closedPayload)) {
                messagingTemplate.convertAndSend(
                        SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                        closedPayload);
            }
        }

        return DataResponse.toResponseEntity(ChatResponse.CHATROOM_LEAVE_SUCCESS, response);
    }

    @GetMapping("/{chatroomId}/messages")
    public ResponseEntity<BaseResponse> getChatMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("chatroomId") Long chatroomId,
            @Nullable @RequestParam Long cursor,
            @Nullable @RequestParam(defaultValue = "20") Long pageSize
    ) {
        ChatMessagePageResponse response = chatFacade.getChatMessages(
                userDetails.getUsername(),
                chatroomId,
                cursor,
                pageSize);

        return DataResponse.toResponseEntity(ChatResponse.GET_CHAT_MESSAGE_SUCCESS, response);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<BaseResponse> markAllChatroomAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        MarkAllChatroomAsReadResult result = realTimeFacade.markAllChatroomAsRead(userDetails.getUsername());

        result.getBroadcastPayloads()
                .forEach(payload -> {
                    if (!Objects.isNull(payload)) {
                        messagingTemplate.convertAndSend(
                                SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + payload.getChatroomId(),
                                payload.mapToBasePayload());
                    }
                });

        return DataResponse.toResponseEntity(ChatResponse.MARK_ALL_CHATROOM_AS_READ_SUCCESS, result.getApiResponse());
    }

    @PatchMapping("/{chatroomId}/host/delegate")
    public ResponseEntity<BaseResponse> delegateHost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @RequestBody @Valid HostDelegationRequest request
    ) {
        HostDelegationResponse apiResponse = chatFacade.delegateHost(userDetails.getUsername(), chatroomId, request);
        BasePayload responsePayload = realTimeFacade.createHostDelegationMessage(
                apiResponse.getPrevHost(),
                apiResponse.getNewHost());

        if (!Objects.isNull(responsePayload)) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                    responsePayload
            );
        }

        return DataResponse.toResponseEntity(ChatResponse.HOST_DELEGATION_SUCCESS, apiResponse);
    }

    @DeleteMapping("/{chatroomId}/members/{userId}")
    public ResponseEntity<BaseResponse> kickChatParticipant(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @PathVariable Long userId
    ) {
        KickChatParticipantResponse apiResponse = chatFacade.kickChatParticipant(
                userDetails.getUsername(),
                chatroomId,
                userId);

        BasePayload basePayload = realTimeFacade.createUserKickMessage(apiResponse.getKickChatParticipant());

        if (!Objects.isNull(basePayload)) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                    basePayload
            );
        }

        return DataResponse.toResponseEntity(ChatResponse.CHAT_PARTICIPANT_KICK_SUCCESS, apiResponse);
    }
}
