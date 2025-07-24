package com.poortorich.category.util;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.fixture.CategoryFixture;
import com.poortorich.user.entity.User;

public class CategoryTestBuilder {

    private Long id = CategoryFixture.VALID_CATEGORY_ID_1;
    private String name = CategoryFixture.VALID_CATEGORY_NAME_1;
    private String color = CategoryFixture.VALID_CATEGORY_COLOR;
    private Boolean visibility = CategoryFixture.CATEGORY_ACTIVE;
    private Boolean isDeleted = CategoryFixture.CATEGORY_NOT_DELETED;

    public static CategoryTestBuilder builder() {
        return new CategoryTestBuilder();
    }

    public CategoryTestBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CategoryTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CategoryTestBuilder color(String color) {
        this.color = color;
        return this;
    }

    public CategoryTestBuilder visibility(Boolean visibility) {
        this.visibility = visibility;
        return this;
    }

    public CategoryTestBuilder isDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }

    public Category build(CategoryType type, User user) {
        return Category.builder()
                .id(id)
                .type(type)
                .name(name)
                .color(color)
                .visibility(visibility)
                .isDeleted(isDeleted)
                .user(user)
                .build();
    }
}
