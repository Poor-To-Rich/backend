package com.poortorich.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenResponse {

    private final String accessToken;
}
