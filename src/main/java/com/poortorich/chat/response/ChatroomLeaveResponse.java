package com.poortorich.chat.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatroomLeaveResponse {

    private Long deleteChatroomId;
}
