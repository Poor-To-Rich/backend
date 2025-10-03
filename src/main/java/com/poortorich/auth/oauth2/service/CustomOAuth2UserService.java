package com.poortorich.auth.oauth2.service;

import com.poortorich.auth.oauth2.domain.model.CustomOAuth2UserDetails;
import com.poortorich.auth.oauth2.domain.model.KakaoResponse;
import com.poortorich.auth.oauth2.redis.KakaoRedisService;
import com.poortorich.category.service.CategoryService;
import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Gender;
import com.poortorich.user.entity.enums.Role;
import com.poortorich.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final KakaoRedisService kakaoRedisService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        KakaoResponse response = new KakaoResponse(oAuth2User.getAttributes());

        User user = saveUser(response);
        return new CustomOAuth2UserDetails(user);
    }

    private User saveUser(KakaoResponse response) {
        String username = response.getProvider() + "_" + response.getProviderId();
        Optional<User> userOptional = userRepository.findByEmail(response.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (username.equals(user.getIdentify())) {
                return user;
            }
            
            kakaoRedisService.saveUser(user);
            user.updateIdentify(username);
            user.updateRole(Role.KAKAO_EXISTING_USER_PENDING);
            return userRepository.save(user);
        }

        return toUserEntity(response, username);
    }

    private User toUserEntity(KakaoResponse response, String username) {
        User user = User.builder()
                .username(username)
                .password(username)
                .name(response.getName())
                .nickname(UUID.randomUUID().toString().replace("-", "").substring(0, 8)
                        + response.getProviderId())
                .email(response.getEmail())
                .gender(Gender.FEMALE)
                .birth(LocalDate.now())
                .profileImage(response.getProfileImage())
                .role(Role.PENDING)
                .identify(username)
                .job("선택안함")
                .build();

        userRepository.save(user);
        categoryService.saveAllDefaultCategories(user);
        return user;
    }
}
