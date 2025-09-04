package com.poortorich.chat.realtime.event.chatroom.detector;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.realtime.event.chatroom.ChatroomUpdateEvent;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
import com.poortorich.chat.request.ChatroomUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatroomUpdateDetector {

    private final ApplicationEventPublisher eventPublisher;

    public void detect(Chatroom chatroom, ChatroomUpdateRequest chatroomUpdateRequest, String newImage) {
        if (isTitleChanged(chatroom.getTitle(), chatroomUpdateRequest.getChatroomTitle())
                || isImageChanged(chatroom.getImage(), newImage)) {
            eventPublisher.publishEvent(new ChatroomUpdateEvent(chatroom, PayloadType.CHATROOM_INFO_UPDATED));
        }
    }

    private boolean isTitleChanged(String currentTitle, String newTitle) {
        return !Objects.equals(currentTitle, newTitle);
    }

    private boolean isImageChanged(String currentImage, String newImage) {
        return !Objects.equals(currentImage, newImage);
    }
}
