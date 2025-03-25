package com.poortorich.user.service;

import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    private final PasswordEncoder passwordEncoder;

    public void save(UserRegistrationRequest userRegistrationRequest, String profileImageUrl) {
        User user = User.builder()
                .profileImage(profileImageUrl)
                .username(userRegistrationRequest.getUsername())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .name(userRegistrationRequest.getName())
                .nickname(userRegistrationRequest.getNickname())
                .email(userRegistrationRequest.getEmail())
                .gender(userRegistrationRequest.getGender())
                .birth(userRegistrationRequest.getBirth())
                .job(userRegistrationRequest.getJob())
                .build();

        userRepository.save(user);
    }
}
