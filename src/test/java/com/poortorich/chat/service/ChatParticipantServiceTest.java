package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chatnotice.request.ChatNoticeStatusUpdateRequest;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatParticipantServiceTest {

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private ChatBuilder chatBuilder;

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
        when(chatBuilder.buildChatParticipant(user, ChatroomRole.HOST, chatroom))
                .thenReturn(chatParticipant);

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

        when(chatParticipantRepository.countParticipantsByChatroom(chatroom)).thenReturn(currentMemberCount);

        Long result = chatParticipantService.countByChatroom(chatroom);

        assertThat(result).isEqualTo(currentMemberCount);
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
        ChatNoticeStatusUpdateRequest request = new ChatNoticeStatusUpdateRequest("TEMP_HIDDEN");
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
        ChatNoticeStatusUpdateRequest request = new ChatNoticeStatusUpdateRequest("TEMP_HIDDEN");
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
        ChatNoticeStatusUpdateRequest request = new ChatNoticeStatusUpdateRequest("INVALID_REQUEST");
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

    @Test
    @DisplayName("회원 아이디와 채팅방으로 채팅방 참여자 조회 성공")
    void findByUserIdAndChatroomSuccess() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .user(user)
                .chatroom(chatroom)
                .build();

        when(chatParticipantRepository.findByUserIdAndChatroom(userId, chatroom))
                .thenReturn(Optional.of(chatParticipant));

        ChatParticipant result = chatParticipantService.findByUserIdAndChatroom(userId, chatroom);

        assertThat(result).isEqualTo(chatParticipant);
        assertThat(result.getUser().getId()).isEqualTo(user.getId());
        assertThat(result.getChatroom()).isEqualTo(chatroom);
    }

    @Test
    @DisplayName("회원 아이디와 채팅방으로 채팅방 참여자 조회 실패")
    void findByUserIdAndChatroomNotFound() {
        Long userId = 1L;
        Chatroom chatroom = Chatroom.builder().build();

        when(chatParticipantRepository.findByUserIdAndChatroom(userId, chatroom)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatParticipantService.findByUserIdAndChatroom(userId, chatroom))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ChatResponse.CHAT_PARTICIPANT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("전체 참여 인원 목록 조회 성공")
    void getAllParticipantsSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant host = ChatParticipant.builder().build();
        ChatParticipant member1 = ChatParticipant.builder().build();
        ChatParticipant member2 = ChatParticipant.builder().build();

        when(chatParticipantRepository.findAllOrderedParticipants(chatroom))
                .thenReturn(List.of(host, member1, member2));

        List<ChatParticipant> result = chatParticipantService.getAllParticipants(chatroom);

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(host);
        assertThat(result.get(1)).isEqualTo(member1);
        assertThat(result.get(2)).isEqualTo(member2);
    }

    @Test
    @DisplayName("참여 인원 검색 성공")
    void searchParticipantsByNicknameSuccess() {
        String keyword = "test";
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant member1 = ChatParticipant.builder().build();
        ChatParticipant member2 = ChatParticipant.builder().build();
        ChatParticipant member3 = ChatParticipant.builder().build();
        ChatParticipant member4 = ChatParticipant.builder().build();

        when(chatParticipantRepository.searchByChatroomAndNickname(chatroom, keyword))
                .thenReturn(List.of(member1, member2, member3, member4));

        List<ChatParticipant> result = chatParticipantService.searchParticipantsByNickname(chatroom, keyword);

        assertThat(result).hasSize(4);
        assertThat(result).containsExactly(member1, member2, member3, member4);
    }

    @Test
    @DisplayName("keyword가 null인 경우 전체 인원 조회 성공")
    void searchParticipantsByNicknameNullSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant member1 = ChatParticipant.builder().build();
        ChatParticipant member2 = ChatParticipant.builder().build();
        ChatParticipant member3 = ChatParticipant.builder().build();
        ChatParticipant member4 = ChatParticipant.builder().build();

        when(chatParticipantRepository.findAllOrderedParticipants(chatroom))
                .thenReturn(List.of(member1, member2, member3, member4));

        List<ChatParticipant> result = chatParticipantService.searchParticipantsByNickname(chatroom, null);

        assertThat(result).hasSize(4);
        assertThat(result).containsExactly(member1, member2, member3, member4);
    }
}
