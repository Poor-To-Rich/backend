package com.poortorich.chatnotice.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatrooms/{chatroomId}/notices")
@RequiredArgsConstructor
public class ChatNoticeController {

    private final ChatFacade chatFacade;

    @PatchMapping
    public ResponseEntity<BaseResponse> updateNoticeStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @RequestBody ChatNoticeUpdateRequest request
    ) {
        chatFacade.updateNoticeStatus(userDetails.getUsername(), chatroomId, request);
        return BaseResponse.toResponseEntity(ChatNoticeResponse.UPDATE_CHAT_NOTICE_STATUS_SUCCESS);
    }
}
