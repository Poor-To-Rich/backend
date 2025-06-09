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

    MALE("MALE"),
    FEMALE("FEMALE");

    private final String value;

    public static Gender from(String value) {
        return Arrays.stream(Gender.values())
                .filter(gender -> Objects.equals(gender.value, value))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(UserResponse.GENDER_INVALID));
    }

    @Override
    public String toString() {
        return value;
    }
}
