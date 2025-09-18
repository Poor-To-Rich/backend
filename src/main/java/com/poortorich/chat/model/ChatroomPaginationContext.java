package com.poortorich.chat.model;

import com.poortorich.user.entity.User;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

@Builder
public record ChatroomPaginationContext(
        User user,
        LocalDateTime cursor,
        PageRequest pageRequest
) {
}
