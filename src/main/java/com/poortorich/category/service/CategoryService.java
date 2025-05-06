package com.poortorich.category.service;

import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.repository.CategoryRepository;
import com.poortorich.category.request.CategoryInfoRequest;
import com.poortorich.category.response.ActiveCategoriesResponse;
import com.poortorich.category.response.CategoryInfoResponse;
import com.poortorich.category.response.CategoryResponse;
import com.poortorich.category.response.CustomCategoryResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.global.response.Response;
import com.poortorich.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<DefaultCategoryResponse> getDefaultCategories(CategoryType type, User user) {
        return categoryRepository.findByTypeAndUser(type, user).stream()
                .map(category -> DefaultCategoryResponse.builder()
                        .name(category.getName())
                        .color(category.getColor())
                        .visibility(category.getVisibility())
                        .build())
                .toList();
    }

    public List<CustomCategoryResponse> getCustomCategories(CategoryType type, User user) {
        return categoryRepository.findByTypeAndUser(type, user).stream()
                .map(category -> CustomCategoryResponse.builder()
                        .id(category.getId())
                        .color(category.getColor())
                        .name(category.getName())
                        .build())
                .toList();
    }

    public ActiveCategoriesResponse getActiveCategories(String type, User user) {
        List<String> categories = categoryRepository.findByTypeAndUser(CategoryType.from(type), user).stream()
                .filter(Category::getVisibility)
                .map(Category::getName)
                .toList();

        return ActiveCategoriesResponse.builder()
                .activeCategories(categories)
                .build();
    }

    public Response createCategory(CategoryInfoRequest customCategory, CategoryType type, User user) {
        if (categoryRepository.findByNameAndUser(customCategory.getName(), user).isPresent()) {
            return CategoryResponse.CATEGORY_NAME_DUPLICATE;
        }

        categoryRepository.save(buildCategory(customCategory, type, user));
        return CategoryResponse.CREATE_CATEGORY_SUCCESS;
    }

    private Category buildCategory(CategoryInfoRequest customCategory, CategoryType type, User user) {
        return Category.builder()
                .type(type)
                .name(customCategory.getName())
                .color(customCategory.getColor())
                .visibility(true)
                .user(user)
                .build();
    }

    public CategoryInfoResponse getCategory(Long id, User user) {
        Category category = getCategoryOrThrow(id, user);

        return CategoryInfoResponse.builder()
                .name(category.getName())
                .color(category.getColor())
                .build();
    }

    @Transactional
    public Response modifyCategory(Long id, CategoryInfoRequest categoryRequest, User user) {
        categoryRepository.findByNameAndUser(categoryRequest.getName(), user)
                .orElseThrow(() -> new BadRequestException(CategoryResponse.CATEGORY_NAME_DUPLICATE));

        Category category = getCategoryOrThrow(id, user);
        category.updateCategory(categoryRequest.getName(), categoryRequest.getColor());

        return CategoryResponse.MODIFY_CATEGORY_SUCCESS;
    }

    public Response deleteCategory(Long id, User user) {
        Category category = getCategoryOrThrow(id, user);
        categoryRepository.delete(category);

        return CategoryResponse.DELETE_CATEGORY_SUCCESS;
    }

    private Category getCategoryOrThrow(Long id, User user) {
        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));
    }

    public Category findCategoryByName(String name, User user) {
        return categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));
    }
}
