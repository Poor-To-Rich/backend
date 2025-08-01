package com.poortorich.category.fixture;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.user.entity.User;

public class CategoryFixture {

    public static final Long VALID_CATEGORY_ID_1 = 1L;
    public static final Long VALID_CATEGORY_ID_2 = 2L;

    public static final String VALID_CATEGORY_NAME_1 = "test1";
    public static final String VALID_CATEGORY_NAME_2 = "test2";

    public static final String VALID_CATEGORY_COLOR = "#ffffff";

    public static final Boolean CATEGORY_ACTIVE = true;
    public static final Boolean CATEGORY_INACTIVE = false;

    public static final Boolean CATEGORY_NOT_DELETED = false;
    public static final Boolean CATEGORY_DELETED = true;

    public static final String CATEGORY_TYPE_EXPENSE = "expense";
    public static final String CATEGORY_TYPE_INCOME = "income";

    private CategoryFixture() {
    }
}
