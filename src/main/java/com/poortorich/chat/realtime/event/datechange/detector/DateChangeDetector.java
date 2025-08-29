package com.poortorich.chat.realtime.event.datechange.detector;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.chat.realtime.event.datechange.DateChangeEvent;
import com.poortorich.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DateChangeDetector {

    private final ChatMessageRepository chatMessageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void detect(Chatroom chatroom) {
        String currentDate = LocalDate.now().toString();

        if (!chatMessageRepository.existsByContentAndMessageTypeAndChatroom(currentDate, MessageType.DATE, chatroom)) {
            eventPublisher.publishEvent(new DateChangeEvent(chatroom.getId()));
        }
    }
}
