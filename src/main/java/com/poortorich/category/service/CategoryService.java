package com.poortorich.category.service;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.CategoryType;
import com.poortorich.category.repository.CategoryRepository;
import com.poortorich.category.request.CategoryInfoRequest;
import com.poortorich.category.response.CategoryInfoResponse;
import com.poortorich.category.response.CategoryResponse;
import com.poortorich.category.response.CustomCategoryResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.category.validator.CategoryValidator;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
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

    public Response createCategory(CategoryInfoRequest customCategory, CategoryType type) {
        if (categoryValidator.isNameUsed(customCategory.getName())) {
            return CategoryResponse.DUPLICATION_CATEGORY_NAME;
        }

        categoryRepository.save(buildCategory(customCategory, type));
        return CategoryResponse.SUCCESS_CREATE_CATEGORY;
    }

    private Category buildCategory(CategoryInfoRequest customCategory, CategoryType type) {
        return Category.builder()
                .type(type)
                .name(customCategory.getName())
                .color(customCategory.getColor())
                .visibility(true)
                .build();
    }

    public CategoryInfoResponse getCategory(Long id) {
        return categoryRepository.findById(id).stream()
                .map(category -> CategoryInfoResponse.builder()
                        .name(category.getName())
                        .color(category.getColor())
                        .build())
                .findAny().orElseThrow(() -> new NotFoundException(CategoryResponse.NON_EXISTENT_ID));
    }
}
