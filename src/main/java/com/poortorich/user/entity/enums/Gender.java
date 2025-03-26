package com.poortorich.user.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Gender {
    MALE("남"),
    FEMALE("여"),
    OTHER("기타");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
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
