package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.IterationResponse;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum EndType {

    AFTER("after"),
    UNTIL("until"),
    NEVER("never");

    private final String type;

    public static EndType from(String type) {
        return Arrays.stream(EndType.values())
                .filter(end -> Objects.equals(end.type, type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(IterationResponse.END_TYPE_INVALID));
    }

    @Override
    public String toString() {
        return type;
    }
}
