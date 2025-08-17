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

import java.util.List;
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

    @Test
    @DisplayName("최근 공지 목록 조회 성공")
    void getPreviewNoticesSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        ChatNotice chatNotice1 = ChatNotice.builder().chatroom(chatroom).build();
        ChatNotice chatNotice2 = ChatNotice.builder().chatroom(chatroom).build();
        ChatNotice chatNotice3 = ChatNotice.builder().chatroom(chatroom).build();

        when(chatNoticeRepository.findTop3ByChatroomOrderByCreatedDateDesc(chatroom))
                .thenReturn(List.of(chatNotice1, chatNotice2, chatNotice3));

        List<ChatNotice> previewNotice = chatNoticeService.getPreviewNotices(chatroom);

        assertThat(previewNotice).hasSize(3);
        assertThat(previewNotice.get(0)).isEqualTo(chatNotice1);
        assertThat(previewNotice.get(1)).isEqualTo(chatNotice2);
        assertThat(previewNotice.get(2)).isEqualTo(chatNotice3);
    }

    @Test
    @DisplayName("채팅방과 아이디로 공지 조회 성공")
    void findNoticeSuccess() {
        Long noticeId = 1L;
        Chatroom chatroom = Chatroom.builder().build();
        ChatNotice chatNotice = ChatNotice.builder()
                .id(noticeId)
                .chatroom(chatroom)
                .build();

        when(chatNoticeRepository.findByChatroomAndId(chatroom, noticeId)).thenReturn(Optional.of(chatNotice));

        ChatNotice result = chatNoticeService.findNotice(chatroom, noticeId);

        assertThat(result.getId()).isEqualTo(noticeId);
        assertThat(result.getChatroom()).isEqualTo(chatroom);
    }

    @Test
    @DisplayName("공지가 없는 경우 예외 발생")
    void findNoticeNotFound() {
        Long noticeId = 1L;
        Chatroom chatroom = Chatroom.builder().build();

        when(chatNoticeRepository.findByChatroomAndId(chatroom, noticeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatNoticeService.findNotice(chatroom, noticeId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ChatNoticeResponse.NOTICE_NOT_FOUND.getMessage());
    }
}
