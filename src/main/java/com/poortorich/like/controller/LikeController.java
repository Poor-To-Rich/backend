package com.poortorich.like.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.like.facade.LikeFacade;
import com.poortorich.like.request.LikeUpdateRequest;
import com.poortorich.like.response.enums.LikeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatrooms/{chatroomId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeFacade likeFacade;

    @GetMapping
    public ResponseEntity<BaseResponse> getChatroomLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId
    ) {
        return DataResponse.toResponseEntity(
                LikeResponse.GET_LIKE_STATUS_SUCCESS,
                likeFacade.getChatroomLike(userDetails.getUsername(), chatroomId)
        );
    }

    @PatchMapping
    public ResponseEntity<BaseResponse> updateChatroomLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @RequestBody @Valid LikeUpdateRequest request
    ) {
        return DataResponse.toResponseEntity(
                LikeResponse.UPDATE_LIKE_STATUS_SUCCESS,
                likeFacade.updateChatroomLike(userDetails.getUsername(), chatroomId, request)
        );
    }
}
