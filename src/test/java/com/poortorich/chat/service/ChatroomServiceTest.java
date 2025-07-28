package com.poortorich.chat.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.repository.ChatroomRepository;
import com.poortorich.chat.request.ChatroomCreateRequest;
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
                null, chatroomTitle, maxMemberCount, null, null, isRankingEnabled, chatroomPassword
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
}
