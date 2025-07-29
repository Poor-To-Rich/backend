package com.poortorich.chat.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatController {

    private final ChatFacade chatFacade;

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
}
