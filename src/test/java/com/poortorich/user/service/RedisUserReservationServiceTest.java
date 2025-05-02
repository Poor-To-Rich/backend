package com.poortorich.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class RedisUserReservationServiceTest {

    private static final String USERNAME = "testUser";
    private static final String NICKNAME = "testNick";
    private static final String USERNAME_KEY = "username_reservation:testUser";
    private static final String NICKNAME_KEY = "nickname_reservation:testNick";

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisUserReservationService userReservationService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("사용자명이 주어졌을 때 레디스에 사용자명이 저장된다.")
    void reservedUsername_shouldSaveReservedUsernameToRedis_whenUsernameIsGiven() {
        userReservationService.reservedUsername(USERNAME);

        verify(valueOperations).set(
                eq(USERNAME_KEY),
                eq(Boolean.TRUE.toString()),
                eq(10L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("닉네임이 주어졌을 때 레디스에 닉네임이 저장된다.")
    void reservedNickname_shouldSaveReservedNicknameToRedis_whenNicknameIsGiven() {
        userReservationService.reservedNickname(NICKNAME);

        verify(valueOperations).set(
                eq(NICKNAME_KEY),
                eq(Boolean.TRUE.toString()),
                eq(10L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("레디스에 존재하는 사용자명이 주어졌을 때 true를 반환한다.")
    void existsByUsername_shouldReturnTrue_whenUsernameIsExists() {
        when(redisTemplate.hasKey(USERNAME_KEY)).thenReturn(true);

        assertThat(userReservationService.existsByUsername(USERNAME)).isTrue();

        verify(redisTemplate).hasKey(USERNAME_KEY);
    }

    @Test
    @DisplayName("레디스에 존재하지않는 사용자명이 주어졌을 때, false를 반환한다.")
    void existsByUsername_shouldReturnFalse_whenUsernameIsNotExists() {
        when(redisTemplate.hasKey(USERNAME_KEY)).thenReturn(false);

        assertThat(userReservationService.existsByUsername(USERNAME)).isFalse();

        verify(redisTemplate).hasKey(USERNAME_KEY);
    }

    @Test
    @DisplayName("레디스에 존재하는 닉네임이 주어졌을 때, true를 반환한다.")
    void existsByNickname_shouldReturnTrue_whenNicknameIsExists() {
        when(redisTemplate.hasKey(NICKNAME_KEY)).thenReturn(true);

        assertThat(userReservationService.existsByNickname(NICKNAME)).isTrue();

        verify(redisTemplate).hasKey(NICKNAME_KEY);
    }

    @Test
    @DisplayName("레디스에 존재하지않는 닉네임이 주저였을 때, false를 반환한다.")
    void existsByNickname_shouldReturnFalse_whenNicknameIsNotExists() {
        when(redisTemplate.hasKey(NICKNAME_KEY)).thenReturn(false);

        assertThat(userReservationService.existsByNickname(NICKNAME)).isFalse();

        verify(redisTemplate).hasKey(NICKNAME_KEY);
    }

    @Test
    @DisplayName("사용자명 삭제 테스트")
    void removeUsernameReservation_shouldDoNothing_whenUsernameIsGiven() {
        userReservationService.removeUsernameReservation(USERNAME);

        verify(redisTemplate).delete(USERNAME_KEY);
    }

    @Test
    @DisplayName("닉네임 삭제 테스트")
    void removeNicknameReservation_shouldDoNothing_whenNicknameIsGiven() {
        userReservationService.removeNicknameReservation(NICKNAME);

        verify(redisTemplate).delete(NICKNAME_KEY);
    }

}
