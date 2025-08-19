package com.poortorich.chat.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatParticipantController {

    private final ChatFacade chatFacade;

    @GetMapping("/users/hosted-chatrooms")
    public ResponseEntity<BaseResponse> getHostedChatrooms(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponse.toResponseEntity(
                ChatResponse.GET_HOSTED_CHATROOMS_SUCCESS,
                chatFacade.getHostedChatrooms(userDetails.getUsername())
        );
    }
}
