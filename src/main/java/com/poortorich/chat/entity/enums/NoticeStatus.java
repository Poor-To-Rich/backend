package com.poortorich.chat.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeStatus {

    DEFAULT,
    TEMP_HIDDEN,        // 접어두기
    PERMANENT_HIDDEN    // 다신 열지 않음
}
