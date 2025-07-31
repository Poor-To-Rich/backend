package com.poortorich.chat.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatroomUpdateResponse {

    private final Long chatroomId;
}
