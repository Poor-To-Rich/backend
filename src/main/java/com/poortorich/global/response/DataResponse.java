package com.poortorich.global.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuperBuilder
@Getter
public class DataResponse extends BaseResponse {

    private final Object data;

    public static ResponseEntity<BaseResponse> toResponseEntity(Response response, Object data) {
        if (data == null) {
            return BaseResponse.toResponseEntity(response);
        }

        return ResponseEntity.status(response.getHttpStatus())
                .body(DataResponse.builder()
                        .status(response.getHttpStatus().value())
                        .message(response.getMessage())
                        .data(data)
                        .build());
    }

    public static ResponseEntity<BaseResponse> toResponseEntity(HttpStatus httpStatus, String message, Object data) {
        if (data == null) {
            return BaseResponse.toResponseEntity(httpStatus, message);
        }
        
        return ResponseEntity.status(httpStatus)
                .body(DataResponse.builder()
                        .status(httpStatus.value())
                        .message(message)
                        .data(data)
                        .build());
    }
}
