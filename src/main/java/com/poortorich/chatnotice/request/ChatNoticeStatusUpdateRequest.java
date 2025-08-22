package com.poortorich.chatnotice.request;

import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chatnotice.constants.ChatNoticeResponseMessage;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.exceptions.BadRequestException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatNoticeStatusUpdateRequest {

    @NotNull(message = ChatNoticeResponseMessage.NOTICE_STATUS_REQUIRED)
    private String status;

    public NoticeStatus parseStatus() {
        try {
            return NoticeStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ChatNoticeResponse.NOTICE_STATUS_INVALID);
        }
    }
}
