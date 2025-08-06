package com.poortorich.chat.request;

import com.poortorich.chat.constants.ChatResponseMessage;
import com.poortorich.chat.constants.ChatValidationConstraints;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ChatroomUpdateRequest {

    private final MultipartFile chatroomImage;

    private final Boolean isDefaultImage;

    @NotNull(message = ChatResponseMessage.CHATROOM_TITLE_REQUIRED)
    @Size(max = ChatValidationConstraints.CHATROOM_TITLE_MAX,
            message = ChatResponseMessage.CHATROOM_TITLE_TOO_BIG)
    private final String chatroomTitle;

    @NotNull(message = ChatResponseMessage.CHATROOM_MAX_MEMBER_COUNT_REQUIRED)
    @Min(value = ChatValidationConstraints.CHATROOM_MAX_MEMBER_COUNT_MIN,
            message = ChatResponseMessage.CHATROOM_MAX_MEMBER_COUNT_TOO_SMALL)
    @Max(value = ChatValidationConstraints.CHATROOM_MAX_MEMBER_COUNT_MAX,
            message = ChatResponseMessage.CHATROOM_MAX_MEMBER_COUNT_TOO_BIG)
    private final Long maxMemberCount;

    @NotNull(message = ChatResponseMessage.CHATROOM_DESCRIPTION_REQUIRED)
    @Size(max = ChatValidationConstraints.CHATROOM_DESCRIPTION_MAX,
            message = ChatResponseMessage.CHATROOM_DESCRIPTION_TOO_BIG)
    private final String description;

    @Size(max = ChatValidationConstraints.TAG_COUNT_MAX,
            message = ChatResponseMessage.TAG_TOO_MANY)
    private final List<String> hashtags;

    private final Boolean isRankingEnabled;
    private final String chatroomPassword;
}
