package com.poortorich.category.response;

import com.poortorich.category.constants.CategoryResponseMessage;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum CategoryResponse implements Response {

    GET_DEFAULT_EXPENSE_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_DEFAULT_EXPENSE_CATEGORIES_SUCCESS),
    GET_DEFAULT_INCOME_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_DEFAULT_INCOME_CATEGORIES_SUCCESS),
    GET_CUSTOM_EXPENSE_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_CUSTOM_EXPENSE_CATEGORIES_SUCCESS),
    GET_CUSTOM_INCOME_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_CUSTOM_INCOME_CATEGORIES_SUCCESS),
    GET_CUSTOM_CATEGORY_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_CUSTOM_CATEGORY_SUCCESS),
    GET_ACTIVE_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_ACTIVE_CATEGORIES_SUCCESS),

    CREATE_CATEGORY_SUCCESS(HttpStatus.CREATED, CategoryResponseMessage.CREATE_CATEGORY_SUCCESS),
    MODIFY_CATEGORY_SUCCESS(HttpStatus.CREATED, CategoryResponseMessage.MODIFY_CATEGORY_SUCCESS),
    DELETE_CATEGORY_SUCCESS(HttpStatus.OK, CategoryResponseMessage.DELETE_CATEGORY_SUCCESS),

    CATEGORY_NAME_DUPLICATE(HttpStatus.CONFLICT, CategoryResponseMessage.CATEGORY_NAME_DUPLICATE),
    CATEGORY_NON_EXISTENT(HttpStatus.NOT_FOUND, CategoryResponseMessage.CATEGORY_NON_EXISTENT),
    CATEGORY_TYPE_INVALID(HttpStatus.BAD_REQUEST,CategoryResponseMessage.CATEGORY_TYPE_INVALID),

    DEFAULT(HttpStatus.INTERNAL_SERVER_ERROR, CategoryResponseMessage.DEFAULT);

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
