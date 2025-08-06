package com.poortorich.like.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.like.entity.Like;
import com.poortorich.like.repository.LikeRepository;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public Boolean getLikeStatus(User user, Chatroom chatroom) {
        return likeRepository.findByUserAndChatroom(user, chatroom)
                .map(Like::getLikeStatus)
                .orElse(false);
    }

    public Long getLikeCount(Chatroom chatroom) {
        return likeRepository.countByChatroomAndLikeStatusTrue(chatroom);
    }
}
