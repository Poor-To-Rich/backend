package com.poortorich.chat.request;

import com.poortorich.chat.constants.ChatResponseMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatroomLikeUpdateRequest {

    @NotNull(message = ChatResponseMessage.IS_LIKED_REQUIRED)
    private Boolean isLiked;
}
