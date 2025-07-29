package com.poortorich.chat.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.repository.ChatroomRepository;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.exceptions.NotFoundException;
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
class ChatroomServiceTest {

    @Mock
    private ChatroomRepository chatroomRepository;

    @InjectMocks
    private ChatroomService chatroomService;

    @Captor
    private ArgumentCaptor<Chatroom> chatroomCaptor;

    @Test
    @DisplayName("채팅방 저장 성공")
    void createChatroomSuccess() {
        String imageUrl = "https://image.com";
        String chatroomTitle = "채팅방";
        Long maxMemberCount = 10L;
        Boolean isRankingEnabled = false;
        String chatroomPassword = "부자12";
        ChatroomCreateRequest request = new ChatroomCreateRequest(
                chatroomTitle, maxMemberCount, null, null, isRankingEnabled, chatroomPassword
        );

        chatroomService.createChatroom(imageUrl, request);

        verify(chatroomRepository).save(chatroomCaptor.capture());
        Chatroom savedChatroom = chatroomCaptor.getValue();

        assertThat(savedChatroom.getImage()).isEqualTo(imageUrl);
        assertThat(savedChatroom.getTitle()).isEqualTo(chatroomTitle);
        assertThat(savedChatroom.getMaxMemberCount()).isEqualTo(maxMemberCount);
        assertThat(savedChatroom.getIsRankingEnabled()).isEqualTo(isRankingEnabled);
        assertThat(savedChatroom.getPassword()).isEqualTo(chatroomPassword);
    }

    @Test
    @DisplayName("아이디 값으로 채팅방 조회 성공")
    void findChatroomByIdSuccess() {
        Long chatroomId = 1L;
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();

        when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));

        Chatroom result = chatroomService.findChatroomById(chatroomId);

        assertThat(result).isEqualTo(chatroom);
    }

    @Test
    @DisplayName("존재하지 않는 아이디 값으로 조회시 예외 발생")
    void findChatroomByIdNotFound() {
        Long chatroomId = 1L;
        when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatroomService.findChatroomById(chatroomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ChatResponse.CHATROOM_NON_EXISTENT.getMessage());
    }
}
