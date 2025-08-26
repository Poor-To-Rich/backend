package com.poortorich.chat.model;

import com.poortorich.user.entity.User;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;

@Builder
public record ChatroomPaginationContext(
        User user,
        Long cursor,
        PageRequest pageRequest
) {
}
