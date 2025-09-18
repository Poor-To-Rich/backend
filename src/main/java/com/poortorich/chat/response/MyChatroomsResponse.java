package com.poortorich.chat.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MyChatroomsResponse {

    private boolean hasNext;
    private LocalDateTime nextCursor;
    private List<MyChatroom> chatrooms;
}
