package com.poortorich.chat.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.response.MyChatroomsResponse;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserChatroomController {

    private final ChatFacade chatFacade;

    @GetMapping("/me/chatrooms")
    public ResponseEntity<BaseResponse> getMyChatrooms(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long cursor
    ) {
        MyChatroomsResponse response = chatFacade.getMyChatrooms(userDetails.getUsername(), cursor);
        return DataResponse.toResponseEntity(ChatResponse.GET_MY_CHATROOMS_SUCCESS, response);
    }
}
