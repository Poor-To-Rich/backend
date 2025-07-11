package com.poortorich.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsernameResponse {

    private final String username;
}
