package com.poortorich.user.util;

import com.poortorich.chat.realtime.event.user.UserProfileUpdateEvent;
import com.poortorich.user.entity.User;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.service.UserService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileUpdateEventDetector {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public void detectEvent(String username, ProfileUpdateRequest updateRequest, String newProfileImage) {
        User user = userService.findUserByUsername(username);

        if (isNicknameChanged(user, updateRequest.getNickname()) || isProfileImageChanged(user, newProfileImage)) {
            eventPublisher.publishEvent(new UserProfileUpdateEvent(username));
        }
    }

    private boolean isNicknameChanged(User user, String newNickname) {
        return !Objects.equals(user.getNickname(), newNickname);
    }

    private boolean isProfileImageChanged(User user, String newProfileImage) {
        return !Objects.equals(user.getProfileImage(), newProfileImage);
    }
}
