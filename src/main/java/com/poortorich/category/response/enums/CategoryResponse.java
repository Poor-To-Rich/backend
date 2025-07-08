package com.poortorich.category.response.enums;

import com.poortorich.category.constants.CategoryResponseMessage;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum CategoryResponse implements Response {

    GET_DEFAULT_EXPENSE_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_DEFAULT_EXPENSE_CATEGORIES_SUCCESS, null),
    GET_DEFAULT_INCOME_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_DEFAULT_INCOME_CATEGORIES_SUCCESS, null),
    GET_CUSTOM_EXPENSE_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_CUSTOM_EXPENSE_CATEGORIES_SUCCESS, null),
    GET_CUSTOM_INCOME_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_CUSTOM_INCOME_CATEGORIES_SUCCESS, null),
    GET_CUSTOM_CATEGORY_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_CUSTOM_CATEGORY_SUCCESS, null),
    GET_ACTIVE_CATEGORIES_SUCCESS(HttpStatus.OK, CategoryResponseMessage.GET_ACTIVE_CATEGORIES_SUCCESS, null),

    CATEGORY_VISIBILITY_TRUE_SUCCESS(HttpStatus.OK, CategoryResponseMessage.CATEGORY_VISIBILITY_TRUE_SUCCESS, null),
    CATEGORY_VISIBILITY_FALSE_SUCCESS(HttpStatus.OK, CategoryResponseMessage.CATEGORY_VISIBILITY_FALSE_SUCCESS, null),

    CREATE_CATEGORY_SUCCESS(HttpStatus.CREATED, CategoryResponseMessage.CREATE_CATEGORY_SUCCESS, null),
    MODIFY_CATEGORY_SUCCESS(HttpStatus.CREATED, CategoryResponseMessage.MODIFY_CATEGORY_SUCCESS, null),
    DELETE_CATEGORY_SUCCESS(HttpStatus.OK, CategoryResponseMessage.DELETE_CATEGORY_SUCCESS, null),

    CATEGORY_NAME_DUPLICATE(HttpStatus.CONFLICT, CategoryResponseMessage.CATEGORY_NAME_DUPLICATE, "name"),
    CATEGORY_NON_EXISTENT(HttpStatus.NOT_FOUND, CategoryResponseMessage.CATEGORY_NON_EXISTENT, "categoryId"),
    CATEGORY_TYPE_INVALID(HttpStatus.BAD_REQUEST,CategoryResponseMessage.CATEGORY_TYPE_INVALID, "categoryType"),

    DEFAULT(HttpStatus.INTERNAL_SERVER_ERROR, CategoryResponseMessage.DEFAULT, null);

    private final HttpStatus httpStatus;
    private final String message;
    private final String field;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getField() {
        return field;
    }
}
