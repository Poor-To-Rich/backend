package com.poortorich.like.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.like.request.LikeUpdateRequest;
import com.poortorich.like.response.LikeStatusResponse;
import com.poortorich.like.service.LikeService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeFacadeTest {

    @Mock
    private UserService userService;
    @Mock
    private ChatroomService chatroomService;
    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeFacade likeFacade;

    @Test
    @DisplayName("채팅방 좋아요 상태 조회 성공")
    void getChatroomLikeSuccess() {
        String username = "testUser";
        Long chatroomId = 1L;
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(likeService.getLikeStatus(user, chatroom)).thenReturn(true);
        when(likeService.getLikeCount(chatroom)).thenReturn(3L);

        LikeStatusResponse result = likeFacade.getChatroomLike(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getIsLiked()).isTrue();
        assertThat(result.getLikeCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("좋아요 상태 변경 성공")
    void updateChatroomLikeSuccess() {
        String username = "testUser";
        Long chatroomId = 1L;
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();
        LikeUpdateRequest request = new LikeUpdateRequest(true);

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(likeService.getLikeStatus(user, chatroom)).thenReturn(true);
        when(likeService.getLikeCount(chatroom)).thenReturn(3L);

        LikeStatusResponse result = likeFacade.updateChatroomLike(username, chatroomId, request);

        verify(likeService).updateLikeStatus(user, chatroom, request.getIsLiked());
        assertThat(result).isNotNull();
        assertThat(result.getIsLiked()).isTrue();
        assertThat(result.getLikeCount()).isEqualTo(3L);
    }
}
