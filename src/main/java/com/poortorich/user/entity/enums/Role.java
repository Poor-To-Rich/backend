package com.poortorich.user.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.user.response.enums.UserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "일반회원"),
    TEST("ROLE_TEST", "테스트용"),
    PENDING("ROLE_PENDING", "보류중"),
    KAKAO_EXISTING_USER_PENDING("ROLE_KAKAO_EXISTING_USER_PENDING", "카카오기존회원보류"),
    WITHDRAW("ROLE_WITHDRAW", "회원탈퇴");

    private final String value;
    private final String description;

    public static Role from(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BadRequestException(UserResponse.USER_ROLE_INVALID);
        }
    }
}
