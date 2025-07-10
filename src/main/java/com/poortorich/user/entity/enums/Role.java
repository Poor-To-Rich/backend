package com.poortorich.user.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.user.response.enums.UserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("관리자"),
    USER("일반회원"),
    TEST("테스트용");

    private final String description;

    public static Role from(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BadRequestException(UserResponse.USER_ROLE_INVALID);
        }
    }
}
