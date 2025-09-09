package com.poortorich.chatnotice.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.response.ChatParticipantProfile;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.response.AllNoticesResponse;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.response.NoticeDetailsResponse;
import com.poortorich.chatnotice.response.NoticeResponse;
import com.poortorich.chatnotice.response.PreviewNoticeResponse;
import com.poortorich.chatnotice.response.PreviewNoticesResponse;
import com.poortorich.chatnotice.service.ChatNoticeService;
import com.poortorich.chatnotice.util.ChatNoticeBuilder;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatNoticeFacadeTest {

    private final String username = "test";
    private final Long chatroomId = 1L;
    @Mock
    private ChatroomService chatroomService;
    @Mock
    private ChatParticipantService chatParticipantService;
    @Mock
    private ChatNoticeService chatNoticeService;
    @Mock
    private ChatNoticeBuilder noticeBuilder;
    @InjectMocks
    private ChatNoticeFacade chatNoticeFacade;
    private User user;
    private Chatroom chatroom;
    private ChatParticipant chatParticipant;
    private ChatNotice chatNotice;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username(username)
                .nickname(username)
                .build();

        chatroom = Chatroom.builder()
                .id(chatroomId)
                .build();

        chatParticipant = ChatParticipant.builder()
                .user(user)
                .chatroom(chatroom)
                .noticeStatus(NoticeStatus.DEFAULT)
                .role(ChatroomRole.HOST)
                .rankingStatus(RankingStatus.NONE)
                .build();

        chatNotice = ChatNotice.builder()
                .id(1L)
                .chatroom(chatroom)
                .author(chatParticipant)
                .content("공지 내용")
                .createdDate(LocalDateTime.of(2025, 8, 8, 0, 0))
                .build();
    }

    @Test
    @DisplayName("최근 공지 조회 성공")
    void getLatestNoticeSuccess() {
        LatestNoticeResponse expectedResponse = LatestNoticeResponse.builder()
                .status(chatParticipant.getNoticeStatus().name())
                .noticeId(chatNotice.getId())
                .preview(chatNotice.getContent())
                .createdAt(chatNotice.getCreatedDate().toString())
                .authorNickname(user.getNickname())
                .build();

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.findByUsernameAndChatroom(username, chatroom)).thenReturn(chatParticipant);
        when(chatNoticeService.getLatestNotice(chatroom)).thenReturn(chatNotice);
        when(noticeBuilder.buildLatestNoticeResponse(chatParticipant.getNoticeStatus(), chatNotice))
                .thenReturn(expectedResponse);

        LatestNoticeResponse result = chatNoticeFacade.getLatestNotice(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getNoticeId()).isEqualTo(chatNotice.getId());
        assertThat(result.getStatus()).isEqualTo(chatParticipant.getNoticeStatus().toString());
        assertThat(result.getPreview()).isEqualTo(chatNotice.getContent());
        assertThat(result.getAuthorNickname()).isEqualTo(user.getNickname());
        assertThat(result.getCreatedAt()).isEqualTo(chatNotice.getCreatedDate().toString());
    }

    @Test
    @DisplayName("공지 내용이 30자 초과인 경우 30자만 표출")
    void getLatestNoticePreviewTest() {
        String preview = "코딩하다가 정신 차려보니 새벽 3시가 되었다. 피곤하지";
        chatNotice = ChatNotice.builder()
                .id(1L)
                .chatroom(chatroom)
                .author(chatParticipant)
                .content("코딩하다가 정신 차려보니 새벽 3시가 되었다. 피곤하지만 너무 재밌어서 멈출수가 없다.")
                .createdDate(LocalDateTime.of(2025, 8, 8, 0, 0))
                .build();

        LatestNoticeResponse expectedResponse = LatestNoticeResponse.builder()
                .status(chatParticipant.getNoticeStatus().name())
                .noticeId(chatNotice.getId())
                .preview(preview)
                .createdAt(chatNotice.getCreatedDate().toString())
                .authorNickname(user.getNickname())
                .build();
        when(noticeBuilder.buildLatestNoticeResponse(chatParticipant.getNoticeStatus(), chatNotice))
                .thenReturn(expectedResponse);

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.findByUsernameAndChatroom(username, chatroom)).thenReturn(chatParticipant);
        when(chatNoticeService.getLatestNotice(chatroom)).thenReturn(chatNotice);

        LatestNoticeResponse result = chatNoticeFacade.getLatestNotice(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getNoticeId()).isEqualTo(chatNotice.getId());
        assertThat(result.getStatus()).isEqualTo(chatParticipant.getNoticeStatus().toString());
        assertThat(result.getPreview()).isEqualTo(preview);
        assertThat(result.getPreview()).hasSize(30);
        assertThat(result.getAuthorNickname()).isEqualTo(user.getNickname());
        assertThat(result.getCreatedAt()).isEqualTo(chatNotice.getCreatedDate().toString());
    }

    @Test
    @DisplayName("최근 공지 목록 조회 성공")
    void getPreviewNoticesSuccess() {
        ChatNotice chatNotice2 = ChatNotice.builder()
                .id(2L)
                .chatroom(chatroom)
                .author(chatParticipant)
                .content("공지 내용")
                .createdDate(LocalDateTime.of(2025, 8, 8, 0, 0))
                .build();
        PreviewNoticeResponse notice1Response = PreviewNoticeResponse.builder()
                .noticeId(chatNotice.getId())
                .preview(chatNotice.getContent())
                .build();
        PreviewNoticeResponse notice2Response = PreviewNoticeResponse.builder()
                .noticeId(chatNotice2.getId())
                .preview(chatNotice2.getContent())
                .build();
        List<PreviewNoticeResponse> expectedResponses = List.of(notice1Response, notice2Response);

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatNoticeService.getPreviewNotices(chatroom)).thenReturn(List.of(chatNotice, chatNotice2));
        when(noticeBuilder.buildPreviewNoticeResponse(List.of(chatNotice, chatNotice2)))
                .thenReturn(expectedResponses);

        PreviewNoticesResponse result = chatNoticeFacade.getPreviewNotices(chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getNotices()).hasSize(2);
        assertThat(result.getNotices().get(0).getNoticeId()).isEqualTo(chatNotice.getId());
        assertThat(result.getNotices().get(0).getPreview()).isEqualTo(chatNotice.getContent());
        assertThat(result.getNotices().get(1).getNoticeId()).isEqualTo(chatNotice2.getId());
        assertThat(result.getNotices().get(1).getPreview()).isEqualTo(chatNotice2.getContent());
    }

    @Test
    @DisplayName("최근 공지 중 공지 내용이 30자 초과인 경우 30자만 표출")
    void getPreviewNoticesPreviewTest() {
        String preview = "코딩하다가 정신 차려보니 새벽 3시가 되었다. 피곤하지";
        ChatNotice chatNotice2 = ChatNotice.builder()
                .id(2L)
                .chatroom(chatroom)
                .author(chatParticipant)
                .content("코딩하다가 정신 차려보니 새벽 3시가 되었다. 피곤하지만 너무 재밌어서 멈출수가 없다.")
                .createdDate(LocalDateTime.of(2025, 8, 8, 0, 0))
                .build();
        PreviewNoticeResponse notice1Response = PreviewNoticeResponse.builder()
                .noticeId(chatNotice.getId())
                .preview(chatNotice.getContent())
                .build();
        PreviewNoticeResponse notice2Response = PreviewNoticeResponse.builder()
                .noticeId(chatNotice2.getId())
                .preview(preview)
                .build();
        List<PreviewNoticeResponse> expectedResponses = List.of(notice1Response, notice2Response);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatNoticeService.getPreviewNotices(chatroom)).thenReturn(List.of(chatNotice, chatNotice2));
        when(noticeBuilder.buildPreviewNoticeResponse(List.of(chatNotice, chatNotice2)))
                .thenReturn(expectedResponses);
        PreviewNoticesResponse result = chatNoticeFacade.getPreviewNotices(chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getNotices()).hasSize(2);
        assertThat(result.getNotices().get(0).getNoticeId()).isEqualTo(chatNotice.getId());
        assertThat(result.getNotices().get(0).getPreview()).isEqualTo(chatNotice.getContent());
        assertThat(result.getNotices().get(1).getNoticeId()).isEqualTo(chatNotice2.getId());
        assertThat(result.getNotices().get(1).getPreview()).isEqualTo(preview);
    }

    @Test
    @DisplayName("최근 공지가 없는 경우 빈 목록 반환")
    void getPreviewNoticesEmpty() {
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatNoticeService.getPreviewNotices(chatroom)).thenReturn(List.of());
        when(noticeBuilder.buildPreviewNoticeResponse(List.of())).thenReturn(List.of());

        PreviewNoticesResponse result = chatNoticeFacade.getPreviewNotices(chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getNotices()).isEmpty();
    }

    @Test
    @DisplayName("최근 공지가 null인 경우 null response 반환")
    void getLatestNoticeNull() {
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.findByUsernameAndChatroom(username, chatroom)).thenReturn(chatParticipant);
        when(chatNoticeService.getLatestNotice(chatroom)).thenReturn(null);
        when(noticeBuilder.buildLatestNoticeResponse(chatParticipant.getNoticeStatus(), null))
                .thenReturn(null);
        LatestNoticeResponse result = chatNoticeFacade.getLatestNotice(username, chatroomId);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("공지 상세 조회 성공")
    void getNoticeDetailsSuccess() {
        Long noticeId = 1L;
        ChatParticipantProfile authorProfile = ChatParticipantProfile.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .rankingType(chatParticipant.getRankingStatus())
                .isHost(ChatroomRole.HOST.equals(chatParticipant.getRole()))
                .build();

        NoticeDetailsResponse expectedResponse = NoticeDetailsResponse.builder()
                .noticeId(chatNotice.getId())
                .content(chatNotice.getContent())
                .createdAt(chatNotice.getCreatedDate().toString())
                .author(authorProfile)
                .build();
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatNoticeService.findNotice(chatroom, noticeId)).thenReturn(chatNotice);
        when(noticeBuilder.buildNoticeDetailsResponse(chatNotice))
                .thenReturn(expectedResponse);
        NoticeDetailsResponse result = chatNoticeFacade.getNoticeDetails(chatroomId, noticeId);

        assertThat(result).isNotNull();
        assertThat(result.getNoticeId()).isEqualTo(noticeId);
        assertThat(result.getContent()).isEqualTo(chatNotice.getContent());
        assertThat(result.getCreatedAt()).isEqualTo(chatNotice.getCreatedDate().toString());
        assertThat(result.getAuthor().getUserId()).isEqualTo(user.getId());
        assertThat(result.getAuthor().getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("전체 공지 목록 조회 성공")
    void getAllNoticesSuccess() {
        Long cursor = Long.MAX_VALUE;
        Pageable pageable = PageRequest.of(0, 20);
        NoticeResponse notice1Response = NoticeResponse.builder()
                .noticeId(chatNotice.getId())
                .preview(chatNotice.getContent())
                .authorNickname(user.getNickname())
                .createdAt(chatNotice.getCreatedDate().toString())
                .build();

        ChatNotice chatNotice2 = ChatNotice.builder()
                .id(4L)
                .author(chatParticipant)
                .chatroom(chatroom)
                .content("공지 내용")
                .createdDate(LocalDateTime.of(2025, 8, 8, 0, 0))
                .build();
        NoticeResponse notice2Response = NoticeResponse.builder()
                .noticeId(chatNotice2.getId())
                .preview(chatNotice2.getContent())
                .authorNickname(user.getNickname())
                .createdAt(chatNotice2.getCreatedDate().toString())
                .build();

        List<NoticeResponse> noticeResponses = List.of(notice1Response, notice2Response);

        AllNoticesResponse expectedResponse = AllNoticesResponse.builder()
                .hasNext(true)
                .nextCursor(chatNotice2.getId())
                .notices(noticeResponses)
                .build();

        Slice<ChatNotice> slice = new SliceImpl<>(List.of(chatNotice, chatNotice2), pageable, true);

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatNoticeService.getAllNoticeByCursor(chatroom, cursor, pageable)).thenReturn(slice);
        when(noticeBuilder.buildAllNoticesResponse(true, chatNotice2.getId(), List.of(chatNotice, chatNotice2)))
                .thenReturn(expectedResponse);
        AllNoticesResponse result = chatNoticeFacade.getAllNotices(chatroomId, cursor);

        assertThat(result).isNotNull();
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getNextCursor()).isEqualTo(slice.getContent().getLast().getId());
        assertThat(result.getNotices()).hasSize(2);
        assertThat(result.getNotices().get(0).getNoticeId()).isEqualTo(chatNotice.getId());
        assertThat(result.getNotices().get(0).getPreview()).isEqualTo(chatNotice.getContent());
        assertThat(result.getNotices().get(1).getNoticeId()).isEqualTo(chatNotice2.getId());
        assertThat(result.getNotices().get(1).getPreview()).isEqualTo(chatNotice2.getContent());
    }

    @Test
    @DisplayName("전체 공지 목록 조회 성공 - 마지막 페이지")
    void getAllNoticesLastPageSuccess() {
        Long cursor = Long.MAX_VALUE;
        Pageable pageable = PageRequest.of(0, 20);
        ChatNotice chatNotice2 = ChatNotice.builder()
                .id(4L)
                .author(chatParticipant)
                .chatroom(chatroom)
                .content("공지 내용")
                .createdDate(LocalDateTime.of(2025, 8, 8, 0, 0))
                .build();
        NoticeResponse notice1Response = NoticeResponse.builder()
                .noticeId(chatNotice.getId())
                .preview(chatNotice.getContent())
                .authorNickname(user.getNickname())
                .createdAt(chatNotice.getCreatedDate().toString())
                .build();

        NoticeResponse notice2Response = NoticeResponse.builder()
                .noticeId(chatNotice2.getId())
                .preview(chatNotice2.getContent())
                .authorNickname(user.getNickname())
                .createdAt(chatNotice2.getCreatedDate().toString())
                .build();

        List<NoticeResponse> noticeResponses = List.of(notice1Response, notice2Response);
        AllNoticesResponse expectedResponse = AllNoticesResponse.builder()
                .hasNext(false)
                .nextCursor(null)
                .notices(noticeResponses)
                .build();
        Slice<ChatNotice> slice = new SliceImpl<>(List.of(chatNotice, chatNotice2), pageable, false);

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatNoticeService.getAllNoticeByCursor(chatroom, cursor, pageable)).thenReturn(slice);
        when(noticeBuilder.buildAllNoticesResponse(false, null, List.of(chatNotice, chatNotice2)))
                .thenReturn(expectedResponse);
        AllNoticesResponse result = chatNoticeFacade.getAllNotices(chatroomId, cursor);

        assertThat(result).isNotNull();
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getNextCursor()).isNull();
        assertThat(result.getNotices()).hasSize(2);
        assertThat(result.getNotices().get(0).getNoticeId()).isEqualTo(chatNotice.getId());
        assertThat(result.getNotices().get(0).getPreview()).isEqualTo(chatNotice.getContent());
        assertThat(result.getNotices().get(1).getNoticeId()).isEqualTo(chatNotice2.getId());
        assertThat(result.getNotices().get(1).getPreview()).isEqualTo(chatNotice2.getContent());
    }
}
