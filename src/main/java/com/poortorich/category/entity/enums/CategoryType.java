package com.poortorich.category.entity.enums;

import com.poortorich.category.response.CategoryResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum CategoryType {

    DEFAULT_EXPENSE("expense"),
    DEFAULT_INCOME("income"),
    CUSTOM_EXPENSE("expense"),
    CUSTOM_INCOME("income");

    private final String type;

    public static CategoryType from(String type) {
        return Arrays.stream(CategoryType.values())
                .filter(category -> Objects.equals(category.type, type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(CategoryResponse.INVALID_CATEGORY_TYPE));
    }
}
