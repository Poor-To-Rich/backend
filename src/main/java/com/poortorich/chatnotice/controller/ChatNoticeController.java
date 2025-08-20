package com.poortorich.chatnotice.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chatnotice.facade.ChatNoticeFacade;
import com.poortorich.chatnotice.model.NoticeCreateResult;
import com.poortorich.chatnotice.model.NoticeUpdateResult;
import com.poortorich.chatnotice.request.ChatNoticeCreateRequest;
import com.poortorich.chatnotice.request.ChatNoticeStatusUpdateRequest;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/chatrooms/{chatroomId}/notices")
@RequiredArgsConstructor
public class ChatNoticeController {

    private final ChatFacade chatFacade;
    private final ChatNoticeFacade chatNoticeFacade;

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<BaseResponse> createNewNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @RequestBody ChatNoticeCreateRequest noticeCreateRequest
    ) {
        NoticeCreateResult result = chatNoticeFacade.create(userDetails.getUsername(), chatroomId, noticeCreateRequest);
        if (!Objects.isNull(result.getBroadcastPayload())) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                    result.getBroadcastPayload());
        }
        return DataResponse.toResponseEntity(ChatNoticeResponse.CHAT_NOTICE_CREATE_SUCCESS, result.getApiResponse());
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<BaseResponse> updateNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @PathVariable Long noticeId,
            @RequestBody ChatNoticeUpdateRequest noticeUpdateRequest
    ) {
        NoticeUpdateResult result = chatNoticeFacade.update(
                userDetails.getUsername(),
                chatroomId,
                noticeId,
                noticeUpdateRequest);
        if (!Objects.isNull(result.getBroadcastPayload())) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                    result.getBroadcastPayload()
            );
        }
        return DataResponse.toResponseEntity(ChatNoticeResponse.CHAT_NOTICE_UPDATE_SUCCESS, result.getApiResponse());
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<BaseResponse> deleteNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @PathVariable Long noticeId
    ) {
        BasePayload basePayload = chatNoticeFacade.deleteNotice(userDetails.getUsername(), chatroomId, noticeId);

        if (!Objects.isNull(basePayload)) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                    basePayload);
        }
        return BaseResponse.toResponseEntity(ChatNoticeResponse.CHAT_NOTICE_DELETE_SUCCESS);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getLatestNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId
    ) {
        return DataResponse.toResponseEntity(
                ChatNoticeResponse.GET_LATEST_NOTICE_SUCCESS,
                chatNoticeFacade.getLatestNotice(userDetails.getUsername(), chatroomId)
        );
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<BaseResponse> getNoticeDetails(@PathVariable Long chatroomId, @PathVariable Long noticeId) {
        return DataResponse.toResponseEntity(
                ChatNoticeResponse.GET_NOTICE_DETAILS_SUCCESS,
                chatNoticeFacade.getNoticeDetails(chatroomId, noticeId)
        );
    }

    @PatchMapping
    public ResponseEntity<BaseResponse> updateNoticeStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @RequestBody @Valid ChatNoticeStatusUpdateRequest request
    ) {
        chatFacade.updateNoticeStatus(userDetails.getUsername(), chatroomId, request);
        return BaseResponse.toResponseEntity(ChatNoticeResponse.UPDATE_NOTICE_STATUS_SUCCESS);
    }

    @GetMapping("/preview")
    public ResponseEntity<BaseResponse> getPreviewNotices(@PathVariable Long chatroomId) {
        return DataResponse.toResponseEntity(
                ChatNoticeResponse.GET_PREVIEW_NOTICE_SUCCESS,
                chatNoticeFacade.getPreviewNotices(chatroomId)
        );
    }
}
