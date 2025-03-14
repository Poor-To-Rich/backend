package com.poortorich.category.service;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.CategoryType;
import com.poortorich.category.repository.CategoryRepository;
import com.poortorich.category.request.CustomCategoryRequest;
import com.poortorich.category.response.CategoryResponse;
import com.poortorich.category.response.CustomCategoryResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.category.validator.CategoryValidator;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;

    public List<DefaultCategoryResponse> getDefaultCategories(CategoryType type) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getType() == type)
                .map(category -> DefaultCategoryResponse.builder()
                        .name(category.getName())
                        .color(category.getColor())
                        .visibility(category.getVisibility())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CustomCategoryResponse> getCustomCategories(CategoryType type) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getType() == type)
                .map(category -> CustomCategoryResponse.builder()
                        .id(category.getId())
                        .color(category.getColor())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public Response createCategory(CustomCategoryRequest customCategory, CategoryType type) {
        if (categoryValidator.isNameUsed(customCategory.getName())) {
            return CategoryResponse.DUPLICATION_CATEGORY_NAME;
        }

        categoryRepository.save(buildCategory(customCategory, type));
        return CategoryResponse.SUCCESS_CREATE_CATEGORY;
    }

    private Category buildCategory(CustomCategoryRequest customCategory, CategoryType type) {
        return Category.builder()
                .type(type)
                .name(customCategory.getName())
                .color(customCategory.getColor())
                .visibility(true)
                .build();
    }
}
