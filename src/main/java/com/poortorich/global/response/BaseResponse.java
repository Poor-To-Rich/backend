package com.poortorich.global.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuperBuilder
@Getter
public class BaseResponse {

    private final Integer resultCode;

    private final String resultMessage;

    public static ResponseEntity<BaseResponse> toResponseEntity(Response response) {
        BaseResponse baseResponse = BaseResponse.builder()
                .resultCode(response.getHttpStatus().value())
                .resultMessage(response.getMessage())
                .build();

        return ResponseEntity.status(response.getHttpStatus()).body(baseResponse);
    }

    public static ResponseEntity<BaseResponse> toResponseEntity(HttpStatus httpStatus, String message) {
        BaseResponse baseResponse = BaseResponse.builder()
                .resultCode(httpStatus.value())
                .resultMessage(message)
                .build();

        return ResponseEntity.status(httpStatus.value()).body(baseResponse);
    }
}
