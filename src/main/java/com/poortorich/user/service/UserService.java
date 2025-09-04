package com.poortorich.user.service;

import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.s3.constants.S3Constants;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Role;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.request.EmailUpdateRequest;
import com.poortorich.user.request.PasswordResetRequest;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.UserDetailResponse;
import com.poortorich.user.response.UserEmailResponse;
import com.poortorich.user.response.UsernameResponse;
import com.poortorich.user.response.enums.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileService;

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

        boolean isDefaultProfile = S3Constants.DEFAULT_PROFILES.contains(user.getProfileImage());
        String profileImage = null;
        if (!isDefaultProfile) {
            profileImage = user.getProfileImage();
        }

        return UserDetailResponse.builder()
                .userId(user.getId())
                .profileImage(profileImage)
                .isDefaultProfile(isDefaultProfile)
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

    public void updateEmail(String username, EmailUpdateRequest emailUpdateRequest) {
        User user = findUserByUsername(username);
        user.updateEmail(emailUpdateRequest.getEmail());
    }

    @Transactional
    public void deleteUserAccount(User user) {
        String profileImage = user.getProfileImage();
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        user.withdraw(password);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                fileService.deleteImage(profileImage);
            }
        });
    }

    public UsernameResponse findUsernameByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));

        return UsernameResponse.builder()
                .username(user.getUsername())
                .build();
    }

    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        userRepository.findByEmail(passwordResetRequest.getEmail())
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND))
                .updatePassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
    }

    public void updateRole(String username, Role role) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND))
                .updateRole(role);
    }

    public List<User> findAllById(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return userIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
