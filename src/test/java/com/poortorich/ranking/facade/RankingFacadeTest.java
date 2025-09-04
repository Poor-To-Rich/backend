package com.poortorich.ranking.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.response.enums.RankingResponse;
import com.poortorich.ranking.service.RankingService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingFacadeTest {

    @Mock
    private UserService userService;
    @Mock
    private ChatroomService chatroomService;
    @Mock
    private ChatParticipantService chatParticipantService;
    @Mock
    private ChatParticipantValidator chatParticipantValidator;
    @Mock
    private RankingService rankingService;

    @InjectMocks
    private RankingFacade rankingFacade;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDate lastMonday = now
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .toLocalDate();

    @Test
    @DisplayName("최신 랭킹 조회 성공")
    void getLatestRankingSuccess() {
        String username = "test";
        Long chatroomId = 1L;
        Long saverId = 1L;
        Long flexerId = 2L;
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();
        Ranking ranking = Ranking.builder()
                .id(1L)
                .chatroom(chatroom)
                .saverFirst(saverId)
                .flexerFirst(flexerId)
                .createdDate(LocalDateTime.of(2025, 8, 18, 0, 0, 0))
                .build();
        User saverUser = User.builder()
                .id(saverId)
                .profileImage("")
                .nickname("saver")
                .build();
        ChatParticipant saver = ChatParticipant.builder()
                .id(saverId)
                .user(saverUser)
                .role(ChatroomRole.MEMBER)
                .rankingStatus(RankingStatus.SAVER)
                .build();
        User flexerUser = User.builder()
                .id(flexerId)
                .profileImage("")
                .nickname("flexer")
                .build();
        ChatParticipant flexer = ChatParticipant.builder()
                .id(flexerId)
                .user(flexerUser)
                .role(ChatroomRole.MEMBER)
                .rankingStatus(RankingStatus.FLEXER)
                .build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(rankingService.findLatestRanking(eq(chatroom), eq(lastMonday.atStartOfDay()), any(LocalDateTime.class)))
                .thenReturn(ranking);
        when(chatParticipantService.findByUserIdAndChatroom(saverId, chatroom)).thenReturn(saver);
        when(chatParticipantService.findByUserIdAndChatroom(flexerId, chatroom)).thenReturn(flexer);

        var result = rankingFacade.getLatestRanking(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.found()).isTrue();
        assertThat(result.response().getRankingId()).isEqualTo(ranking.getId());
        assertThat(result.response().getRankedAt()).isEqualTo(ranking.getCreatedDate().toString());
        assertThat(result.response().getSaver().getUserId()).isEqualTo(saverId);
        assertThat(result.response().getFlexer().getUserId()).isEqualTo(flexerId);
    }

    @Test
    @DisplayName("최신 랭킹이 없는 경우 null 반환")
    void getLatestRankingNull() {
        String username = "test";
        Long chatroomId = 1L;
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(rankingService.findLatestRanking(eq(chatroom), eq(lastMonday.atStartOfDay()), any(LocalDateTime.class)))
                .thenReturn(null);

        var result = rankingFacade.getLatestRanking(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.found()).isFalse();
        assertThat(result.response().getRankedAt()).isEqualTo(lastMonday.atStartOfDay().toString());
        assertThat(result.response().getRankingId()).isNull();
        assertThat(result.response().getSaver()).isNull();
        assertThat(result.response().getFlexer()).isNull();
    }
}
