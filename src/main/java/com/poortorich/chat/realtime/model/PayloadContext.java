package com.poortorich.chat.realtime.model;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.user.entity.User;
import lombok.Builder;

@Builder
public record PayloadContext(User user, Chatroom chatroom, ChatParticipant chatParticipant) {
}
