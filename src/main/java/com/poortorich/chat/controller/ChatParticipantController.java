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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/chatrooms/{chatroomId}/members/all")
    public ResponseEntity<BaseResponse> getAllParticipants(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId
    ) {
        return DataResponse.toResponseEntity(
                ChatResponse.GET_ALL_PARTICIPANTS_SUCCESS,
                chatFacade.getAllParticipants(userDetails.getUsername(), chatroomId)
        );
    }

    @GetMapping("/chatrooms/{chatroomId}/members/search")
    public ResponseEntity<BaseResponse> searchParticipants(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @RequestParam(required = false) String nickname
    ) {
        return DataResponse.toResponseEntity(
                ChatResponse.SEARCH_PARTICIPANTS_SUCCESS,
                chatFacade.searchParticipantsByKeyword(userDetails.getUsername(), chatroomId, nickname)
        );
    }
}
