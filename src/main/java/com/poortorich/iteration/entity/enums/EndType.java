package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.IterationResponse;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public enum EndType {

    AFTER("after"),
    UNTIL("until"),
    NEVER("never");

    private final String type;

    public static EndType from(String type) {
        for (EndType end : EndType.values()) {
            if (Objects.equals(end.type, type)) {
                return end;
            }
        }

        throw new BadRequestException(IterationResponse.END_TYPE_INVALID);
    }
}
