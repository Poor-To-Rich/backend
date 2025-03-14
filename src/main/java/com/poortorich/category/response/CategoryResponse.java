package com.poortorich.category.response;

import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum CategoryResponse implements Response {
    SUCCESS_CREATE_CATEGORY(HttpStatus.CREATED, "카테고리를 성공적으로 등록하였습니다."),

    DUPLICATION_CATEGORY_NAME(HttpStatus.CONFLICT, "이미 사용중인 카테고리 이름입니다."),

    DEFAULT(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러 발생");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
