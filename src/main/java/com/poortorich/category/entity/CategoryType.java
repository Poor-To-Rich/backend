package com.poortorich.category.entity;

import com.poortorich.category.response.CategoryResponse;
import com.poortorich.global.exceptions.BadRequestException;

import java.util.Map;

public enum CategoryType {
    DEFAULT_EXPENSE,
    DEFAULT_INCOME,
    CUSTOM_EXPENSE,
    CUSTOM_INCOME;

    public static final Map<String, CategoryType> TYPE_MAP = Map.of(
            "income", DEFAULT_INCOME,
            "expense", DEFAULT_EXPENSE
    );

    public static CategoryType from(String type) {
        if (!TYPE_MAP.containsKey(type)) {
            throw new BadRequestException(CategoryResponse.INVALID_CATEGORY_TYPE);
        }

        return TYPE_MAP.get(type);
    }
}
