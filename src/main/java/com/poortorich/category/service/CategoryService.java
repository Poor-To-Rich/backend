package com.poortorich.category.service;

import com.poortorich.category.domain.model.enums.DefaultExpenseCategory;
import com.poortorich.category.domain.model.enums.DefaultIncomeCategory;
import com.poortorich.category.entity.Category;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.repository.CategoryRepository;
import com.poortorich.category.request.CategoryInfoRequest;
import com.poortorich.category.request.CategoryVisibilityRequest;
import com.poortorich.category.response.ActiveCategoriesResponse;
import com.poortorich.category.response.CategoryInfoResponse;
import com.poortorich.category.response.enums.CategoryResponse;
import com.poortorich.category.response.CustomCategoryResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.global.response.Response;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public List<DefaultCategoryResponse> getDefaultCategories(CategoryType type, String username) {
        return categoryRepository.findByUserAndTypeAndIsDeletedFalse(userService.findUserByUsername(username), type).stream()
                .map(category -> DefaultCategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .color(category.getColor())
                        .visibility(category.getVisibility())
                        .build())
                .toList();
    }

    public List<CustomCategoryResponse> getCustomCategories(CategoryType type, String username) {
        return categoryRepository.findByUserAndTypeAndIsDeletedFalse(userService.findUserByUsername(username), type).stream()
                .map(category -> CustomCategoryResponse.builder()
                        .id(category.getId())
                        .color(category.getColor())
                        .name(category.getName())
                        .build())
                .toList();
    }

    public ActiveCategoriesResponse getActiveCategories(String type, String username) {
        List<String> categories
                = categoryRepository.findByUserAndTypeInAndIsDeletedFalse(
                        userService.findUserByUsername(username),
                        CategoryType.from(type).getSameGroupTypes())
                .stream()
                .filter(Category::getVisibility)
                .map(Category::getName)
                .sorted((a, b) -> {
                    if (a.equals("기타")) return 1;
                    if (b.equals("기타")) return -1;
                    return 0;
                })
                .toList();

        return ActiveCategoriesResponse.builder()
                .activeCategories(categories)
                .build();
    }

    @Transactional
    public Response updateActiveCategory(
            Long categoryId,
            CategoryVisibilityRequest visibilityRequest,
            String username
    ) {
        Boolean visibility = visibilityRequest.getVisibility();
        categoryRepository.findByIdAndUserAndIsDeletedFalse(categoryId, userService.findUserByUsername(username))
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT))
                .updateVisibility(visibility);

        if (visibility) {
            return CategoryResponse.CATEGORY_VISIBILITY_TRUE_SUCCESS;
        }
        return CategoryResponse.CATEGORY_VISIBILITY_FALSE_SUCCESS;
    }

    public Response createCategory(CategoryInfoRequest customCategory, CategoryType type, String username) {
        User user = userService.findUserByUsername(username);

        validateNameDuplication(user, customCategory.getName(), type.getSameGroupTypes());

        categoryRepository.save(buildCategory(customCategory, type, user));
        return CategoryResponse.CREATE_CATEGORY_SUCCESS;
    }

    private void validateNameDuplication(User user, String name, List<CategoryType> type) {
        if (categoryRepository.findByUserAndNameAndTypeInAndIsDeletedFalse(user, name, type).isPresent()) {
            throw new BadRequestException(CategoryResponse.CATEGORY_NAME_DUPLICATE);
        }
    }

    public void saveAllDefaultCategories(User user) {
        categoryRepository.saveAll(
                Arrays.stream(DefaultExpenseCategory.values())
                        .map(category -> category.toCategoryEntity(user))
                        .toList()
        );

        categoryRepository.saveAll(
                Arrays.stream(DefaultIncomeCategory.values())
                        .map(category -> category.toCategoryEntity(user))
                        .toList()
        );
    }

    private Category buildCategory(CategoryInfoRequest customCategory, CategoryType type, User user) {
        return Category.builder()
                .type(type)
                .name(customCategory.getName())
                .color(customCategory.getColor())
                .visibility(true)
                .user(user)
                .isDeleted(Boolean.FALSE)
                .build();
    }

    public CategoryInfoResponse getCategory(Long id, String username) {
        Category category = categoryRepository
                .findByIdAndUserAndIsDeletedFalse(id, userService.findUserByUsername(username))
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));

        return CategoryInfoResponse.builder()
                .name(category.getName())
                .color(category.getColor())
                .build();
    }

    @Transactional
    public Response modifyCategory(Long id, CategoryInfoRequest categoryRequest, String username) {
        User user = userService.findUserByUsername(username);
        Category category = categoryRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));

        validateNameDuplicationExcludingId(user, categoryRequest.getName(), category.getType().getSameGroupTypes(), id);

        category.updateCategory(categoryRequest.getName(), categoryRequest.getColor());

        return CategoryResponse.MODIFY_CATEGORY_SUCCESS;
    }

    private void validateNameDuplicationExcludingId(User user, String name, List<CategoryType> type, Long id) {
        if (categoryRepository.findByNameExcludingId(user, name, type, id).isPresent()) {
            throw new BadRequestException(CategoryResponse.CATEGORY_NAME_DUPLICATE);
        }
    }

    @Transactional
    public Response deleteCategory(Long id, String username) {
        User user = userService.findUserByUsername(username);
        Category category = categoryRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));

        category.delete();

        return CategoryResponse.DELETE_CATEGORY_SUCCESS;
    }

    public Category findCategoryByName(User user, String name, CategoryType type) {
        return categoryRepository.findByUserAndNameAndTypeInAndIsDeletedFalse(user, name, type.getSameGroupTypes())
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));
    }

    public Category findCategoryByName(String name, User user) {
        return categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));
    }

    public Category getCategoryOrThrow(Long id, User user) {
        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException(CategoryResponse.CATEGORY_NON_EXISTENT));
    }
}
