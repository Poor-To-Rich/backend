package com.poortorich.global.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuperBuilder
@Getter
public class DataResponse extends BaseResponse {

    private final Object data;

    public static ResponseEntity<DataResponse> toResponseEntity(Response response, Object data) {
        return ResponseEntity.status(response.getHttpStatus())
                .body(DataResponse.builder()
                        .resultCode(response.getHttpStatus().value())
                        .resultMessage(response.getMessage())
                        .data(data)
                        .build());
    }

    public static ResponseEntity<DataResponse> toResponseEntity(HttpStatus httpStatus, String message, Object data) {
        return ResponseEntity.status(httpStatus)
                .body(DataResponse.builder()
                        .resultCode(httpStatus.value())
                        .resultMessage(message)
                        .data(data)
                        .build());
    }
}
