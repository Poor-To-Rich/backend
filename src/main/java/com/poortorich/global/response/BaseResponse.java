package com.poortorich.global.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuperBuilder
@Getter
public class BaseResponse {

    private final Integer status;
    private final String message;

    public static ResponseEntity<BaseResponse> toResponseEntity(Response response) {
        if (response.getField() != null) {
            return DataResponse.toResponseEntity(
                    response,
                    ExceptionResponse.builder()
                            .field(response.getField())
                            .build()
            );
        }

        BaseResponse baseResponse = BaseResponse.builder()
                .status(response.getHttpStatus().value())
                .message(response.getMessage())
                .build();

        return ResponseEntity.status(response.getHttpStatus()).body(baseResponse);
    }

    public static ResponseEntity<BaseResponse> toResponseEntity(HttpStatus httpStatus, String message) {
        BaseResponse baseResponse = BaseResponse.builder()
                .status(httpStatus.value())
                .message(message)
                .build();

        return ResponseEntity.status(httpStatus.value()).body(baseResponse);
    }
}
