package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.repository.ChatParticipantRepository;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

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
}
