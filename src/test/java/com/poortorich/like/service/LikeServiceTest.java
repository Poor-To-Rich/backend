package com.poortorich.like.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.like.entity.Like;
import com.poortorich.like.repository.LikeRepository;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @Captor
    private ArgumentCaptor<Like> likeCaptor;

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

        when(likeRepository.countByChatroomAndLikeStatusTrue(chatroom)).thenReturn(likeCount);

        Long result = likeService.getLikeCount(chatroom);

        assertThat(result).isEqualTo(likeCount);
    }

    @Test
    @DisplayName("좋아요 이력이 없는 사용자의 좋아요 상태 변경 성공")
    void updateLikeStatusNoHistorySuccess() {
        Boolean isLiked = true;
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();
        Like like = Like.builder()
                .user(user)
                .chatroom(chatroom)
                .likeStatus(isLiked)
                .build();

        when(likeRepository.findByUserAndChatroom(user, chatroom)).thenReturn(Optional.empty());
        when(likeRepository.save(Mockito.any(Like.class))).thenReturn(like);

        likeService.updateLikeStatus(user, chatroom, isLiked);

        verify(likeRepository).findByUserAndChatroom(user, chatroom);
        verify(likeRepository, times(2)).save(likeCaptor.capture());
        List<Like> savedLikes = likeCaptor.getAllValues();

        Like secondSavedLike = savedLikes.get(1);

        assertThat(secondSavedLike.getUser()).isEqualTo(user);
        assertThat(secondSavedLike.getChatroom()).isEqualTo(chatroom);
        assertThat(secondSavedLike.getLikeStatus()).isEqualTo(isLiked);
    }

    @Test
    @DisplayName("좋아요 취소 이력이 있는 사용자의 좋아요 상태 변경 성공")
    void updateLikeStatusFalseToTrue() {
        Boolean isLiked = true;
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();
        Like beforeLike = Like.builder()
                .user(user)
                .chatroom(chatroom)
                .likeStatus(false)
                .build();

        Like like = Like.builder()
                .user(user)
                .chatroom(chatroom)
                .likeStatus(isLiked)
                .build();

        when(likeRepository.findByUserAndChatroom(user, chatroom)).thenReturn(Optional.of(beforeLike));
        when(likeRepository.save(Mockito.any(Like.class))).thenReturn(like);

        likeService.updateLikeStatus(user, chatroom, isLiked);
        verify(likeRepository).save(likeCaptor.capture());
        Like savedLike = likeCaptor.getValue();

        assertThat(savedLike.getUser()).isEqualTo(user);
        assertThat(savedLike.getChatroom()).isEqualTo(chatroom);
        assertThat(savedLike.getLikeStatus()).isEqualTo(isLiked);
    }
}
