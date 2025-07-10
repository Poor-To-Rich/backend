package com.poortorich.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("관리자"),
    USER("일반회원"),
    TEST("테스트영");

    private final String description;

    public static Role from(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
