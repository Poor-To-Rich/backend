package com.poortorich.category.entity.enums;

import com.poortorich.category.response.CategoryResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public enum CategoryType {

    DEFAULT_EXPENSE("expense"),
    DEFAULT_INCOME("income"),
    CUSTOM_EXPENSE("expense"),
    CUSTOM_INCOME("income");

    private final String type;

    public static CategoryType from(String type) {
        for (CategoryType category : CategoryType.values()) {
            if (Objects.equals(category.type, type)) {
                return category;
            }
        }

        throw new BadRequestException(CategoryResponse.INVALID_CATEGORY_TYPE);
    }
}
