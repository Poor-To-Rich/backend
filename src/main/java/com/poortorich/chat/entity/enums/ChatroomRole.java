package com.poortorich.chat.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatroomRole {

    HOST,
    MEMBER,
    BANNED
}
