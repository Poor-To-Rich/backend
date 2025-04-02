package com.poortorich.user.entity.enums;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("남"),
    FEMALE("여"),
    OTHER("기타");

    private final String value;

    public static Gender from(String value) {
        for (Gender gender : Gender.values()) {
            if (Objects.equals(gender.value, value)) {
                return gender;
            }
        }
        return OTHER;
    }

    @Override
    public String toString() {
        return value;
    }
}
