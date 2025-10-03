package com.poortorich.auth.oauth2.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.auth.model.UserReversionData;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.response.enums.GlobalResponse;
import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class KakaoRedisService {

    private static final String KAKAO_USER_KEY_SUFFIX = "kakao:user:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveUser(User user) {
        UserReversionData data = UserReversionData.builder()
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .gender(user.getGender())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .identify(user.getIdentify())
                .build();
        try {
            String key = getKakaoUserKey(user.getId());
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, value, Duration.ofDays(1));
        } catch (JsonProcessingException exception) {
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }

    public UserReversionData getData(User user) {
        try {
            String key = getKakaoUserKey(user.getId());
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return UserReversionData.builder()
                        .name(user.getName())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .gender(user.getGender())
                        .profileImage(user.getProfileImage())
                        .role(Role.USER)
                        .identify(null)
                        .build();
            }
            return objectMapper.readValue(value, UserReversionData.class);
        } catch (JsonProcessingException exception) {
            System.out.println(exception.getMessage());
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }

    public void deleteUser(User user) {
        String key = getKakaoUserKey(user.getId());
        redisTemplate.delete(key);
    }

    private String getKakaoUserKey(Long userId) {
        return KAKAO_USER_KEY_SUFFIX + userId;
    }
}
