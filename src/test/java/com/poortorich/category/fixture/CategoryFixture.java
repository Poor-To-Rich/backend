package com.poortorich.category.fixture;

import com.poortorich.category.entity.Category;

public class CategoryFixture {

    public static final Long VALID_CATEGORY_ID = 1L;

    public static final String VALID_CATEGORY_NAME = "test";

    public static final String VALID_CATEGORY_COLOR = "#ffffff";

    public static final Boolean CATEGORY_ACTIVE = true;
    public static final Boolean CATEGORY_INACTIVE = false;

    public static final Boolean CATEGORY_NOT_DELETED = false;
    public static final Boolean CATEGORY_DELETED = true;

    public static final String CATEGORY_TYPE_EXPENSE = "expense";
    public static final String CATEGORY_TYPE_INCOME = "income";

    private CategoryFixture() {
    }

    public static Category createDefaultCategory() {
        return Category.builder()
                .id(VALID_CATEGORY_ID)
                .name(VALID_CATEGORY_NAME)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .build();
    }
}
