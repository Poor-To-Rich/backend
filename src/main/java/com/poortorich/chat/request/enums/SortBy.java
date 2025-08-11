package com.poortorich.chat.request.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortBy {

    UPDATED_AT("최근대화순으로 "),
    CREATED_AT("최근생성순으로 "),
    LIKE("좋아요순으로 ");

    private final String message;
}
