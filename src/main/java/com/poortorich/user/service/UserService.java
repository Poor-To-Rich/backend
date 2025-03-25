package com.poortorich.user.service;

import com.poortorich.global.response.Response;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.enums.UserResponse;
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

    public Response registerUser(UserRegistrationRequest userRegistrationRequest) {
        userValidator.validateUsername(userRegistrationRequest.getUsername());
        userValidator.validateNickname(userRegistrationRequest.getNickname());
        userValidator.validateEmail(userRegistrationRequest.getEmail());
        userValidator.validatePasswordMatch(
                userRegistrationRequest.getPassword(),
                userRegistrationRequest.getUserValidationConstraints()
        );

        User user = User.builder()
                .profileImage(userRegistrationRequest.getProfileImage())
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

        return UserResponse.REGISTRATION_SUCCESS;
    }
}
