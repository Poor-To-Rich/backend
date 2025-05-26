package com.poortorich.category.entity;

import com.poortorich.category.domain.model.enums.DefaultExpenseCategory;
import com.poortorich.user.entity.User;
import com.poortorich.user.fixture.UserFixture;

public class CategoryFixture {

    private static final User MOCK_USER = UserFixture.createDefaultUser();

    public static final Category HOUSING = DefaultExpenseCategory.HOUSING.toCategoryEntity(MOCK_USER);

    public static final Category FOOD = DefaultExpenseCategory.FOOD.toCategoryEntity(MOCK_USER);

    public static final Category TRANSPORTATION = DefaultExpenseCategory.TRANSPORTATION.toCategoryEntity(MOCK_USER);

    public static final Category SHOPPING = DefaultExpenseCategory.SHOPPING.toCategoryEntity(MOCK_USER);

    public static final Category HEALTH_MEDICAL = DefaultExpenseCategory.HEALTH_MEDICAL.toCategoryEntity(MOCK_USER);

    public static final Category EDUCATION = DefaultExpenseCategory.EDUCATION.toCategoryEntity(MOCK_USER);

    public static final Category CULTURE_HOBBY = DefaultExpenseCategory.CULTURE_HOBBY.toCategoryEntity(MOCK_USER);

    public static final Category GIFTS_EVENTS = DefaultExpenseCategory.GIFTS_EVENTS.toCategoryEntity(MOCK_USER);

    public static final Category BEAUTY = DefaultExpenseCategory.BEAUTY.toCategoryEntity(MOCK_USER);

    public static final Category CAFE = DefaultExpenseCategory.CAFE.toCategoryEntity(MOCK_USER);

    public static final Category PET_CARE = DefaultExpenseCategory.PET_CARE.toCategoryEntity(MOCK_USER);

    public static final Category EXPENSE_OTHER = DefaultExpenseCategory.OTHER.toCategoryEntity(MOCK_USER);

    public static final Category ALCOHOL_ENTERTAINMENT =
            DefaultExpenseCategory.ALCOHOL_ENTERTAINMENT.toCategoryEntity(MOCK_USER);

    public static final Category SAVINGS_INVESTMENT =
            DefaultExpenseCategory.SAVINGS_INVESTMENT.toCategoryEntity(MOCK_USER);

    public static final Category TRAVEL_ACCOMMODATION =
            DefaultExpenseCategory.TRAVEL_ACCOMMODATION.toCategoryEntity(MOCK_USER);
}
