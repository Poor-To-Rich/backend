package com.poortorich.category.domain.model.enums;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.user.entity.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DefaultIncomeCategory {

    ALLOWANCE("용돈", "#4A90E2"),
    SALARY("월급", "#228B22"),
    BONUS("보너스", "#E5D038"),
    OTHER("기타", "#ADADAD");

    private final static CategoryType type = CategoryType.DEFAULT_INCOME;

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
