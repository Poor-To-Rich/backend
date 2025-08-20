package com.poortorich.chat.model;

import com.poortorich.chat.realtime.payload.response.UserEnterProfileResponsePayload;
import com.poortorich.chat.response.ChatroomEnterResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserEnterChatroomResult {
    
    private final ChatroomEnterResponse apiResponse;
    private final UserEnterProfileResponsePayload broadcastPayload;
}
