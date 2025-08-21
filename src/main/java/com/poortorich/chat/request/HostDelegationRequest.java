package com.poortorich.chat.request;

import com.poortorich.chat.constants.ChatResponseMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HostDelegationRequest {

    @NotNull(message = ChatResponseMessage.TARGET_USER_ID_IS_NULL)
    private Long targetUserId;
}
