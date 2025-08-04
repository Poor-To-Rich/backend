package com.poortorich.chat.response;

import com.poortorich.chat.model.ChatMessageResponse;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessagePageResponse {

    private Long nextCursor;
    private Boolean hasNext;
    private List<ChatMessageResponse> messages;
    private Map<Long, ChatParticipantProfile> users;
}
