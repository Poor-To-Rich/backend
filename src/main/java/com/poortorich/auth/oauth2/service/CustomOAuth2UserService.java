package com.poortorich.auth.oauth2.service;

import com.poortorich.auth.oauth2.domain.model.CustomOAuth2UserDetails;
import com.poortorich.auth.oauth2.domain.model.KakaoResponse;
import com.poortorich.s3.constants.S3Constants;
import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Gender;
import com.poortorich.user.entity.enums.Role;
import com.poortorich.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        OAuth2User oAuth2User = super.loadUser(request);
        KakaoResponse response = new KakaoResponse(oAuth2User.getAttributes());

        User user = saveUser(response);
        System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        return new CustomOAuth2UserDetails(user);
    }

    private User saveUser(KakaoResponse response) {
        System.out.println("□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□");
        String username = response.getProvider() + "_" + response.getProviderId();
        User user = userRepository.findByUsername(username)
                .orElse(User.builder()
                        .username(username)
                        .password("kakao")
                        .name(response.getName())
                        .nickname(UUID.randomUUID().toString().replace("-", "").substring(0, 8)
                                + response.getProviderId())
                        .email(response.getEmail())
                        .gender(Gender.MALE)
                        .birth(LocalDate.now())
                        .profileImage(S3Constants.DEFAULT_PROFILE_IMAGE)
                        .role(Role.USER)
                        .build());
        System.out.println("□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□");
        return userRepository.save(user);
    }
}
