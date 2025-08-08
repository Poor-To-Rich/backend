package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatParticipantServiceTest {

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @InjectMocks
    private ChatParticipantService chatParticipantService;

    @Captor
    private ArgumentCaptor<ChatParticipant> chatParticipantCaptor;

    @Test
    @DisplayName("채팅 참여자 저장 성공")
    void createChatroomHostSuccess() {
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .role(ChatroomRole.HOST)
                .user(user)
                .chatroom(chatroom)
                .build();

        chatParticipantService.createChatroomHost(user, chatroom);

        verify(chatParticipantRepository).save(chatParticipantCaptor.capture());
        ChatParticipant savedChatParticipant = chatParticipantCaptor.getValue();

        assertThat(savedChatParticipant.getRole()).isEqualTo(chatParticipant.getRole());
        assertThat(savedChatParticipant.getUser()).isEqualTo(user);
        assertThat(savedChatParticipant.getChatroom()).isEqualTo(chatroom);
    }

    @Test
    @DisplayName("채팅방 참여 인원 조회 성공")
    void countByChatroomSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        Long currentMemberCount = 5L;

        when(chatParticipantRepository.countByChatroomAndIsParticipatedTrue(chatroom)).thenReturn(currentMemberCount);

        Long result = chatParticipantService.countByChatroom(chatroom);

        assertThat(result).isEqualTo(currentMemberCount);
    }

    @Test
    @DisplayName("유저가 채팅방에 참여중인 경우 true 반환")
    void isJoinedIsParticipatedTrue() {
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant participant = ChatParticipant.builder()
                .user(user)
                .chatroom(chatroom)
                .isParticipated(true)
                .build();

        when(chatParticipantRepository.findByUserAndChatroom(user, chatroom)).thenReturn(Optional.of(participant));

        Boolean result = chatParticipantService.isJoined(user, chatroom);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유저가 채팅방에 참여한 이력은 있지만 현재 참여 중이지 않은 경우 false 반환")
    void isJoinedIsNotParticipatedFalse() {
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant participant = ChatParticipant.builder()
                .user(user)
                .chatroom(chatroom)
                .isParticipated(false)
                .build();

        when(chatParticipantRepository.findByUserAndChatroom(user, chatroom)).thenReturn(Optional.of(participant));

        Boolean result = chatParticipantService.isJoined(user, chatroom);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("유저가 채팅방에 참여한 이력이 없는 경우 false 반환")
    void isJoinedNoParticipationHistoryFalse() {
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();

        when(chatParticipantRepository.findByUserAndChatroom(user, chatroom)).thenReturn(Optional.empty());

        Boolean result = chatParticipantService.isJoined(user, chatroom);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("채팅방 방장 조회 성공")
    void getChatroomHostSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        User user = User.builder().build();
        ChatParticipant hostParticipant = ChatParticipant.builder()
                .user(user)
                .chatroom(chatroom)
                .role(ChatroomRole.HOST)
                .build();

        when(chatParticipantRepository.getChatroomHost(chatroom)).thenReturn(hostParticipant);

        ChatParticipant result = chatParticipantService.getChatroomHost(chatroom);

        assertThat(result).isEqualTo(hostParticipant);
        assertThat(result.getRole()).isEqualTo(ChatroomRole.HOST);
        assertThat(result.getChatroom()).isEqualTo(chatroom);
    }

    @Test
    @DisplayName("username과 채팅방 정보로 채팅방 참여자 조회 성공")
    void findByUsernameAndChatroomIdSuccess() {
        String username = "test";
        Long chatroomId = 1L;
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .user(user)
                .chatroom(chatroom)
                .build();

        when(chatParticipantRepository.findByUsernameAndChatroom(username, chatroom))
                .thenReturn(Optional.of(chatParticipant));

        ChatParticipant result = chatParticipantService.findByUsernameAndChatroom(username, chatroom);

        assertThat(result).isEqualTo(chatParticipant);
        assertThat(result.getUser().getUsername()).isEqualTo(username);
        assertThat(result.getChatroom().getId()).isEqualTo(chatroomId);
    }

    @Test
    @DisplayName("username과 채팅방 정보에 해당하는 참여자가 없는 경우 예외 발생")
    void findByUsernameAndChatroomIdNotFound() {
        String username = "test";
        Long chatroomId = 1L;
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();

        when(chatParticipantRepository.findByUsernameAndChatroom(username, chatroom))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatParticipantService.findByUsernameAndChatroom(username, chatroom))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("공지 상태 변경 성공")
    void updateNoticeStatusSuccess() {
        String username = "test";
        Long chatroomId = 1L;
        ChatNoticeUpdateRequest request = new ChatNoticeUpdateRequest("TEMP_HIDDEN");
        Chatroom chatroom = Chatroom.builder()
                .id(chatroomId)
                .build();
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatroom(chatroom)
                .noticeStatus(NoticeStatus.DEFAULT)
                .build();

        when(chatParticipantRepository.findByUsernameAndChatroom(username, chatroom))
                .thenReturn(Optional.of(chatParticipant));

        chatParticipantService.updateNoticeStatus(username, chatroom, request);

        verify(chatParticipantRepository).save(chatParticipantCaptor.capture());
        ChatParticipant savedChatParticipant = chatParticipantCaptor.getValue();

        assertThat(savedChatParticipant.getNoticeStatus()).isEqualTo(NoticeStatus.TEMP_HIDDEN);
    }

    @Test
    @DisplayName("현 상태가 더 이상 보지 않음인 경우 공지 상태 변경 실패")
    void updateNoticeStatusCurrentStatusPermanentHidden() {
        String username = "test";
        Long chatroomId = 1L;
        ChatNoticeUpdateRequest request = new ChatNoticeUpdateRequest("TEMP_HIDDEN");
        Chatroom chatroom = Chatroom.builder()
                .id(chatroomId)
                .build();
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatroom(chatroom)
                .noticeStatus(NoticeStatus.PERMANENT_HIDDEN)
                .build();

        when(chatParticipantRepository.findByUsernameAndChatroom(username, chatroom))
                .thenReturn(Optional.of(chatParticipant));

        assertThatThrownBy(() -> chatParticipantService.updateNoticeStatus(username, chatroom, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ChatResponse.CHAT_NOTICE_STATUS_IMMUTABLE.getMessage());
    }

    @Test
    @DisplayName("입력값이 유효하지 않은 경우 공지 상태 변경 실패")
    void updateNoticeStatusInvalidRequest() {
        String username = "test";
        Long chatroomId = 1L;
        ChatNoticeUpdateRequest request = new ChatNoticeUpdateRequest("INVALID_REQUEST");
        Chatroom chatroom = Chatroom.builder()
                .id(chatroomId)
                .build();
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatroom(chatroom)
                .noticeStatus(NoticeStatus.DEFAULT)
                .build();

        when(chatParticipantRepository.findByUsernameAndChatroom(username, chatroom))
                .thenReturn(Optional.of(chatParticipant));

        assertThatThrownBy(() -> chatParticipantService.updateNoticeStatus(username, chatroom, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ChatNoticeResponse.NOTICE_STATUS_INVALID.getMessage());
    }
}
