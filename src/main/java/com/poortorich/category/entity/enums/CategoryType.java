package com.poortorich.category.entity.enums;

import com.poortorich.category.response.enums.CategoryResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
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
                .orElseThrow(() -> new BadRequestException(CategoryResponse.CATEGORY_TYPE_INVALID));
    }

    public List<CategoryType> getSameGroupTypes() {
        return Arrays.stream(CategoryType.values())
                .filter(category -> Objects.equals(category.type, this.type))
                .toList();
    }

}

