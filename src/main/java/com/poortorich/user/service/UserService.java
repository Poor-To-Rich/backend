package com.poortorich.user.service;

import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.UserDetailResponse;
import com.poortorich.user.response.UserEmailResponse;
import com.poortorich.user.response.enums.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(UserRegistrationRequest userRegistrationRequest, String profileImageUrl) {
        User user = User.builder()
                .profileImage(profileImageUrl)
                .username(userRegistrationRequest.getUsername())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .name(userRegistrationRequest.getName())
                .nickname(userRegistrationRequest.getNickname())
                .email(userRegistrationRequest.getEmail())
                .gender(userRegistrationRequest.parseGender())
                .birth(userRegistrationRequest.parseBirthday())
                .job(userRegistrationRequest.getJob())
                .build();

        userRepository.save(user);
        
        return user;
    }

    public void update(String username, ProfileUpdateRequest userProfile, String newProfileImage) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND))
                .updateProfile(userProfile, newProfileImage);
    }

    public UserDetailResponse findUserDetailByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));

        return UserDetailResponse.builder()
                .profileImage(user.getProfileImage())
                .name(user.getName())
                .nickname(user.getNickname())
                .birth(user.getBirth().toString())
                .gender(user.getGender().toString())
                .job(user.getJob())
                .build();
    }

    public String findProfileImageByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND))
                .getProfileImage();
    }

    public UserEmailResponse getUserEmail(String username) {
        String userEmail = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND))
                .getEmail();

        return UserEmailResponse.builder()
                .email(userEmail)
                .build();
    }

    public void updatePassword(String username, String newPassword) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND))
                .updatePassword(passwordEncoder.encode(newPassword));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));
    }
}
