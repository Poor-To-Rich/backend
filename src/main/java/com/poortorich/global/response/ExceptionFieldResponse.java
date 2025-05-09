package com.poortorich.global.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionFieldResponse {
    private String field;
}
