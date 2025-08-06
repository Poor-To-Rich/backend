package com.poortorich.like.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.like.entity.Like;
import com.poortorich.like.repository.LikeRepository;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    @DisplayName("좋아요 상태 조회 성공 - 좋아요 이력이 존재하는 경우")
    void getLikeStatusSuccess() {
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();
        Like like = Like.builder()
                .user(user)
                .chatroom(chatroom)
                .likeStatus(true)
                .build();

        when(likeRepository.findByUserAndChatroom(user, chatroom)).thenReturn(Optional.of(like));

        Boolean result = likeService.getLikeStatus(user, chatroom);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("좋아요 상태 조회 성공 - 좋아요 이력이 존재하지 않는 경우")
    void getLikeStatusNoHistorySuccess() {
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();

        when(likeRepository.findByUserAndChatroom(user, chatroom)).thenReturn(Optional.empty());

        Boolean result = likeService.getLikeStatus(user, chatroom);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("좋아요 개수 조회 성공")
    void getLikeCountSuccess() {
        Long likeCount = 10L;
        Chatroom chatroom = Chatroom.builder().build();

        when(likeRepository.countByChatroom(chatroom)).thenReturn(likeCount);

        Long result = likeService.getLikeCount(chatroom);

        assertThat(result).isEqualTo(likeCount);
    }
}
