package com.poortorich.like.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.like.request.LikeUpdateRequest;
import com.poortorich.like.response.LikeStatusResponse;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.like.service.LikeService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final LikeService likeService;

    public LikeStatusResponse getChatroomLike(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        return buildLikeStatusResponse(user, chatroom);
    }

    public LikeStatusResponse updateChatroomLike(
            String username,
            Long chatroomId,
            LikeUpdateRequest request
    ) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        likeService.updateLikeStatus(user, chatroom, request.getIsLiked());

        return buildLikeStatusResponse(user, chatroom);
    }

    private LikeStatusResponse buildLikeStatusResponse(User user, Chatroom chatroom) {
        return LikeStatusResponse.builder()
                .isLiked(likeService.getLikeStatus(user, chatroom))
                .likeCount(likeService.getLikeCount(chatroom))
                .build();
    }
}
