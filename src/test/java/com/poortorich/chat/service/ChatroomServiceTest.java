package com.poortorich.chat.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.repository.ChatroomRepository;
import com.poortorich.chat.repository.RedisChatRepository;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.response.enums.ChatResponse;
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
class ChatroomServiceTest {

    @Mock
    private ChatroomRepository chatroomRepository;
    @Mock
    private RedisChatRepository redisChatRepository;

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

    @Test
    @DisplayName("아이디 값으로 채팅방 조회 성공")
    void findChatroomByIdSuccess() {
        Long chatroomId = 1L;
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();

        when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));

        Chatroom result = chatroomService.findById(chatroomId);

        assertThat(result).isEqualTo(chatroom);
    }

    @Test
    @DisplayName("존재하지 않는 아이디 값으로 조회시 예외 발생")
    void findChatroomByIdNotFound() {
        Long chatroomId = 1L;
        when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatroomService.findById(chatroomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ChatResponse.CHATROOM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("내가 방장인 채팅방 조회 성공")
    void getHostedChatroomsSuccess() {
        User user = User.builder().build();
        Chatroom chatroom1 = Chatroom.builder().build();
        Chatroom chatroom2 = Chatroom.builder().build();

        when(chatroomRepository.findChatroomByUserAndRole(user, ChatroomRole.HOST))
                .thenReturn(List.of(chatroom1, chatroom2));

        List<Chatroom> result = chatroomService.getHostedChatrooms(user);

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(chatroom1);
        assertThat(result.get(1)).isEqualTo(chatroom2);
    }

    @Test
    @DisplayName("Redis 캐시에 데이터가 존재하는 경우 - 조회 성공")
    void getAllChatroomsWithCacheSuccess() {
        String sortBy = "LIKE";
        Long cursor = -1L;
        List<Long> chatroomIds = List.of(1L, 2L, 3L);

        Chatroom chatroom1 = Chatroom.builder().id(1L).build();
        Chatroom chatroom2 = Chatroom.builder().id(2L).build();
        Chatroom chatroom3 = Chatroom.builder().id(3L).build();

        when(redisChatRepository.existsBySortBy(sortBy)).thenReturn(true);
        when(redisChatRepository.getChatroomIds(sortBy, cursor, 20)).thenReturn(chatroomIds);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom1));
        when(chatroomRepository.findById(2L)).thenReturn(Optional.of(chatroom2));
        when(chatroomRepository.findById(3L)).thenReturn(Optional.of(chatroom3));

        List<Chatroom> result = chatroomService.getAllChatrooms(sortBy, cursor);

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(chatroom1);
        assertThat(result.get(1)).isEqualTo(chatroom2);
        assertThat(result.get(2)).isEqualTo(chatroom3);
    }

    @Test
    @DisplayName("Redis 캐시에 데이터가 존재하지 않는 경우 - 캐시 데이터 생성 후 조회 성공 (좋아요순)")
    void getAllChatroomsWithoutCacheSortByLikeSuccess() {
        String sortBy = "LIKE";
        Long cursor = -1L;
        List<Long> chatroomIds = List.of(1L, 2L);

        Chatroom chatroom1 = Chatroom.builder().id(1L).build();
        Chatroom chatroom2 = Chatroom.builder().id(2L).build();

        when(redisChatRepository.existsBySortBy(sortBy)).thenReturn(false);
        when(chatroomRepository.findChatroomsSortByLike()).thenReturn(List.of(chatroom1, chatroom2));
        when(redisChatRepository.getChatroomIds(sortBy, cursor, 20)).thenReturn(chatroomIds);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom1));
        when(chatroomRepository.findById(2L)).thenReturn(Optional.of(chatroom2));

        List<Chatroom> result = chatroomService.getAllChatrooms(sortBy, cursor);

        verify(redisChatRepository).save(sortBy, chatroomIds);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(chatroom1);
        assertThat(result.get(1)).isEqualTo(chatroom2);
    }

    @Test
    @DisplayName("Redis 캐시에 데이터가 존재하지 않는 경우 - 캐시 데이터 생성 후 조회 성공 (최근대화순)")
    void getAllChatroomsWithoutCacheSortByUpdatedAtSuccess() {
        String sortBy = "UPDATED_AT";
        Long cursor = -1L;
        List<Long> chatroomIds = List.of(1L, 2L);

        Chatroom chatroom1 = Chatroom.builder().id(1L).build();
        Chatroom chatroom2 = Chatroom.builder().id(2L).build();

        when(redisChatRepository.existsBySortBy(sortBy)).thenReturn(false);
        when(chatroomRepository.findChatroomsSortByUpdatedAt()).thenReturn(List.of(chatroom1, chatroom2));
        when(redisChatRepository.getChatroomIds(sortBy, cursor, 20)).thenReturn(chatroomIds);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom1));
        when(chatroomRepository.findById(2L)).thenReturn(Optional.of(chatroom2));

        List<Chatroom> result = chatroomService.getAllChatrooms(sortBy, cursor);

        verify(redisChatRepository).save(sortBy, chatroomIds);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(chatroom1);
        assertThat(result.get(1)).isEqualTo(chatroom2);
    }

    @Test
    @DisplayName("Redis 캐시에 데이터가 존재하지 않는 경우 - 캐시 데이터 생성 후 조회 성공 (최근생성순)")
    void getAllChatroomsWithoutCacheSortByCreatedAtSuccess() {
        String sortBy = "CREATED_AT";
        Long cursor = -1L;
        List<Long> chatroomIds = List.of(1L, 2L);

        Chatroom chatroom1 = Chatroom.builder().id(1L).build();
        Chatroom chatroom2 = Chatroom.builder().id(2L).build();

        when(redisChatRepository.existsBySortBy(sortBy)).thenReturn(false);
        when(chatroomRepository.findChatroomsSortByCreatedAt()).thenReturn(List.of(chatroom1, chatroom2));
        when(redisChatRepository.getChatroomIds(sortBy, cursor, 20)).thenReturn(chatroomIds);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom1));
        when(chatroomRepository.findById(2L)).thenReturn(Optional.of(chatroom2));

        List<Chatroom> result = chatroomService.getAllChatrooms(sortBy, cursor);

        verify(redisChatRepository).save(sortBy, chatroomIds);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(chatroom1);
        assertThat(result.get(1)).isEqualTo(chatroom2);
    }


    @Test
    @DisplayName("Redis 캐시에 다음 페이지가 존재하는 경우 - true 반환")
    void hasNextTrueSuccess() {
        String sortBy = "LIKE";
        Long lastChatroomId = 5L;

        when(redisChatRepository.hasNext(sortBy, lastChatroomId)).thenReturn(true);

        Boolean result = chatroomService.hasNext(sortBy, lastChatroomId);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Redis 캐시에서 다음 커서 값을 조회 - 커서 값 반환")
    void getNextCursorSuccess() {
        String sortBy = "LIKE";
        Long lastChatroomId = 5L;
        Long expectedNextCursor = 6L;

        when(redisChatRepository.getNextCursor(sortBy, lastChatroomId)).thenReturn(expectedNextCursor);

        Long result = chatroomService.getNextCursor(sortBy, lastChatroomId);

        assertThat(result).isEqualTo(expectedNextCursor);
    }
}
