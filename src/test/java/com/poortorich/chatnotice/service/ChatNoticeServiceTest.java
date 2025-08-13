package com.poortorich.chatnotice.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.repository.ChatNoticeRepository;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatNoticeServiceTest {

    @Mock
    private ChatNoticeRepository chatNoticeRepository;

    @InjectMocks
    private ChatNoticeService chatNoticeService;

    @Test
    @DisplayName("최근 공지 조회 성공")
    void getLatestNoticeSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        ChatNotice chatNotice = ChatNotice.builder().chatroom(chatroom).build();

        when(chatNoticeRepository.findTop1ByChatroomOrderByCreatedDateDesc(chatroom))
                .thenReturn(Optional.of(chatNotice));

        ChatNotice latestNotice = chatNoticeService.getLatestNotice(chatroom);

        assertThat(latestNotice).isEqualTo(chatNotice);
    }

    @Test
    @DisplayName("공지가 없는 경우 null 반환")
    void getLatestNoticeNotFoundNull() {
        Chatroom chatroom = Chatroom.builder().build();

        when(chatNoticeRepository.findTop1ByChatroomOrderByCreatedDateDesc(chatroom)).thenReturn(Optional.empty());

        ChatNotice latestNotice = chatNoticeService.getLatestNotice(chatroom);

        assertThat(latestNotice).isNull();
    }
}
