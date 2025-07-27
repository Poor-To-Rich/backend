package com.poortorich.chat.request;

import com.poortorich.chat.constants.ChatResponseMessage;
import com.poortorich.chat.constants.ChatValidationConstraints;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatroomCreateRequest {

    @NotNull(message = ChatResponseMessage.CHATROOM_TITLE_REQUIRED)
    @Max(value = ChatValidationConstraints.CHATROOM_TITLE_MAX,
            message = ChatResponseMessage.CHATROOM_TITLE_TOO_BIG)
    private String chatroomTitle;

    @NotNull(message = ChatResponseMessage.CHATROOM_MAX_MEMBER_COUNT_REQUIRED)
    @Min(value = ChatValidationConstraints.CHATROOM_MAX_MEMBER_COUNT_MIN,
            message = ChatResponseMessage.CHATROOM_MAX_MEMBER_COUNT_TOO_SMALL)
    @Max(value = ChatValidationConstraints.CHATROOM_MAX_MEMBER_COUNT_MAX,
            message = ChatResponseMessage.CHATROOM_MAX_MEMBER_COUNT_TOO_BIG)
    private Long maxMemberCount;

    @NotNull(message = ChatResponseMessage.CHATROOM_DESCRIPTION_REQUIRED)
    @Max(value = ChatValidationConstraints.CHATROOM_DESCRIPTION_MAX,
            message = ChatResponseMessage.CHATROOM_DESCRIPTION_TOO_BIG)
    private String description;

    // 각 태그당 50자 제한
    private List<String> hashtags;
    private Boolean isRankingEnabled;
    private String chatroomPassword;
}
