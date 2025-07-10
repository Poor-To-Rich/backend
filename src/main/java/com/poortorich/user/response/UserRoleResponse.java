package com.poortorich.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRoleResponse {
    
    private final String role;
}
