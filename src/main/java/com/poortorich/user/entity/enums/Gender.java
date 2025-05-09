package com.poortorich.user.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.user.response.enums.UserResponse;
import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("MALE", "남자"),
    FEMALE("FEMALE", "여자");

    private final String value;
    private final String koreanValue;

    public static Gender from(String value) {
        return Arrays.stream(Gender.values())
                .filter(gender -> Objects.equals(gender.value, value) || Objects.equals(gender.koreanValue, value))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(UserResponse.GENDER_INVALID, "gender"));
    }

    @Override
    public String toString() {
        return koreanValue;
    }
}
