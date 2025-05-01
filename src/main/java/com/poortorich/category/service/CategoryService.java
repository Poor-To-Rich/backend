package com.poortorich.category.service;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.CategoryType;
import com.poortorich.category.repository.CategoryRepository;
import com.poortorich.category.request.CategoryInfoRequest;
import com.poortorich.category.response.ActiveCategoriesResponse;
import com.poortorich.category.response.CategoryInfoResponse;
import com.poortorich.category.response.CategoryResponse;
import com.poortorich.category.response.CustomCategoryResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.category.validator.CategoryValidator;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.global.response.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .toList();
    }

    public List<CustomCategoryResponse> getCustomCategories(CategoryType type) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getType() == type)
                .map(category -> CustomCategoryResponse.builder()
                        .id(category.getId())
                        .color(category.getColor())
                        .name(category.getName())
                        .build())
                .toList();
    }

    public ActiveCategoriesResponse getActiveCategories(String type) {
        List<String> categories = categoryRepository.findAll().stream()
                .filter(category -> category.getType() == CategoryType.from(type) && category.getVisibility())
                .map(Category::getName)
                .toList();

        return ActiveCategoriesResponse.builder()
                .activeCategories(categories)
                .build();
    }

    public Response createCategory(CategoryInfoRequest customCategory, CategoryType type) {
        if (categoryValidator.isNameUsed(customCategory.getName())) {
            return CategoryResponse.CATEGORY_NAME_DUPLICATE;
        }

        categoryRepository.save(buildCategory(customCategory, type));
        return CategoryResponse.CREATE_CATEGORY_SUCCESS;
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
        Category category = getCategoryOrThrow(id);

        return CategoryInfoResponse.builder()
                .name(category.getName())
                .color(category.getColor())
                .build();
    }

    @Transactional
    public Response modifyCategory(Long id, CategoryInfoRequest categoryRequest) {
        if (categoryValidator.isNameUsed(categoryRequest.getName(), id)) {
            return CategoryResponse.CATEGORY_NAME_DUPLICATE;
        }

        Category category = getCategoryOrThrow(id);
        category.updateCategory(categoryRequest.getName(), categoryRequest.getColor());

        return CategoryResponse.MODIFY_CATEGORY_SUCCESS;
    }

    public Response deleteCategory(Long id) {
        Category category = getCategoryOrThrow(id);
        categoryRepository.delete(category);

        return CategoryResponse.DELETE_CATEGORY_SUCCESS;
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));
    }

    public Category findCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));
    }
}
