package com.poortorich.accountbook.request.enums;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum IterationAction {

    NONE("NONE"),
    THIS_ONLY("THIS_ONLY"),
    THIS_AND_FUTURE("THIS_AND_FUTURE"),
    ALL("ALL");

    private final String action;

    public static IterationAction from(String action) {
        return Arrays.stream(IterationAction.values())
                .filter(iterationAction -> Objects.equals(iterationAction.action, action))
                .findFirst()
                .orElse(NONE);
    }
}
