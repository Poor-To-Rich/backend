package com.poortorich.s3.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.s3.constants.S3Constants.ValidationMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3Response implements Response {

    INVALID_FILE_TYPES(HttpStatus.BAD_REQUEST, ValidationMessages.INVALID_FILE_TYPE),
    FILE_SIZE_INVALID(HttpStatus.BAD_REQUEST, ValidationMessages.FILE_SIZE_INVALID);

    private final HttpStatus httpStatus;
    private final String message;
}
