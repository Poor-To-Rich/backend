package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.repository.ChatMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    @DisplayName("마지막 채팅 시간 조회 성공")
    void getLastMessageTimeSuccess() {
        Chatroom chatroom = Chatroom.builder().id(1L).build();
        ChatMessage lastMessage = ChatMessage.builder()
                .chatroom(chatroom)
                .sentAt(LocalDateTime.of(2025, 7, 31, 2, 30))
                .build();

        when(chatMessageRepository.findTopByChatroomAndTypeInOrderByIdDesc(any(), any())).thenReturn(Optional.of(lastMessage));

        String result = chatMessageService.getLastMessageTime(chatroom);

        assertThat(result).isEqualTo(lastMessage.getSentAt().toString());
    }

    @Test
    @DisplayName("채팅 정보가 없는 경우 null 반환")
    void getLastMessageTimeNull() {
        Chatroom chatroom = Chatroom.builder().id(1L).build();

        String result = chatMessageService.getLastMessageTime(chatroom);

        assertThat(result).isEqualTo(null);
    }
}