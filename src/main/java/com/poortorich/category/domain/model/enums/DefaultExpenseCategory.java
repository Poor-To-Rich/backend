package com.poortorich.category.domain.model.enums;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.user.entity.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DefaultExpenseCategory {

    HOUSING("주거비", "#4A90E2"),
    FOOD("식비", "#7ED321"),
    TRANSPORTATION("교통비", "#F5A623"),
    SHOPPING("쇼핑", "#FF6F61"),
    HEALTH_MEDICAL("건상/의료", "#50E3C2"),
    EDUCATION("교육", "#4A4A8A"),
    CULTURE_HOBBY("문화/취미", "#E563FF"),
    TRAVEL_ACCOMMODATION("여행/숙박", "#2AB6D9"),
    GIFTS_EVENTS("선물/경조사", "#E5D038"),
    BEAUTY("미용", "#FFB1C1"),
    ALCOHOL_ENTERTAINMENT("술/유흥", "#9B51E0"),
    CAFE("카페", "#B88A69"),
    SAVINGS_INVESTMENT("저축/투자", "#228B22"),
    PET_CARE("펫케어", "#E8CEA0"),
    OTHER("기타", "#BDBDBD");

    private static final CategoryType type = CategoryType.DEFAULT_EXPENSE;

    private final String name;
    private final String color;

    public Category toCategoryEntity(User user) {
        return Category.builder()
                .type(type)
                .name(name)
                .color(color)
                .visibility(Boolean.TRUE)
                .user(user)
                .build();
    }
}
