package com.poortorich.like.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.like.entity.Like;
import com.poortorich.like.repository.LikeRepository;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void updateLikeStatus(User user, Chatroom chatroom, Boolean isLiked) {
        Like like = findOrCreateLike(user, chatroom, isLiked);
        like.updateStatus(isLiked);
        likeRepository.save(like);
    }

    private Like findOrCreateLike(User user, Chatroom chatroom, Boolean isLiked) {
        return likeRepository.findByUserAndChatroom(user, chatroom)
                .orElseGet(() -> Like.builder()
                        .user(user)
                        .chatroom(chatroom)
                        .likeStatus(isLiked)
                        .build()
                );
    }

    @Transactional
    public void deleteAllByChatroom(Chatroom chatroom) {
        likeRepository.deleteByChatroom(chatroom);
    }
}
