package com.poortorich.category.response;

import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum CategoryResponse implements Response {
    SUCCESS_CREATE_CATEGORY(HttpStatus.CREATED, "카테고리를 성공적으로 등록하였습니다."),
    SUCCESS_MODIFY_CATEGORY(HttpStatus.CREATED, "카테고리를 성공적으로 편집하였습니다."),
    SUCCESS_DELETE_CATEGORY(HttpStatus.OK, "카테고리를 성공적으로 삭제하였습니다."),

    DUPLICATION_CATEGORY_NAME(HttpStatus.CONFLICT, "이미 사용중인 카테고리 이름입니다."),
    NON_EXISTENT_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    INVALID_CATEGORY_TYPE(HttpStatus.BAD_REQUEST,"카테고리의 타입이 적절하지 않습니다."),

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
