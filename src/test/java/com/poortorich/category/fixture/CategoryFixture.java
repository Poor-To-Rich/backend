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

    public static Category createDefaultExpenseCategory1(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_1)
                .name(VALID_CATEGORY_NAME_1)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.DEFAULT_EXPENSE)
                .user(user)
                .build();
    }

    public static Category createDefaultExpenseCategory2(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_2)
                .name(VALID_CATEGORY_NAME_2)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.DEFAULT_EXPENSE)
                .user(user)
                .build();
    }

    public static Category createDefaultIncomeCategory1(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_1)
                .name(VALID_CATEGORY_NAME_1)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.DEFAULT_INCOME)
                .user(user)
                .build();
    }

    public static Category createDefaultIncomeCategory2(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_2)
                .name(VALID_CATEGORY_NAME_2)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.DEFAULT_INCOME)
                .user(user)
                .build();
    }

    public static Category createCustomExpenseCategory1(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_1)
                .name(VALID_CATEGORY_NAME_1)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.CUSTOM_EXPENSE)
                .user(user)
                .build();
    }

    public static Category createCustomExpenseCategory2(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_2)
                .name(VALID_CATEGORY_NAME_2)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.CUSTOM_EXPENSE)
                .user(user)
                .build();
    }

    public static Category createCustomIncomeCategory1(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_1)
                .name(VALID_CATEGORY_NAME_1)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.CUSTOM_INCOME)
                .user(user)
                .build();
    }

    public static Category createCustomIncomeCategory2(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_2)
                .name(VALID_CATEGORY_NAME_2)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.CUSTOM_INCOME)
                .user(user)
                .build();
    }

    public static Category createInactiveCategory(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_1)
                .name(VALID_CATEGORY_NAME_1)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_INACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .user(user)
                .build();
    }

    public static Category createDefaultCategory(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_1)
                .name(VALID_CATEGORY_NAME_1)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_NOT_DELETED)
                .type(CategoryType.DEFAULT_EXPENSE)
                .user(user)
                .build();
    }

    public static Category createDeletedCategory(User user) {
        return Category.builder()
                .id(VALID_CATEGORY_ID_1)
                .name(VALID_CATEGORY_NAME_1)
                .color(VALID_CATEGORY_COLOR)
                .visibility(CATEGORY_ACTIVE)
                .isDeleted(CATEGORY_DELETED)
                .user(user)
                .build();
    }
}
