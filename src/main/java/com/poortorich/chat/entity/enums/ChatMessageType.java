package com.poortorich.chat.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatMessageType {

    CHAT_MESSAGE,
    SYSTEM_MESSAGE,
    RANKING_STATUS,
    RANKING_MESSAGE
}
