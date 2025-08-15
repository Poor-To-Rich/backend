package com.poortorich.chat.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    TEXT,
    PHOTO,
    RANKING,
    ENTER,
    LEAVE,
    RANKING_STATUS,
    CLOSE,
    DATE
}
