package PoorToRich.PoorToRich.category.service;

import PoorToRich.PoorToRich.category.entity.Category;
import PoorToRich.PoorToRich.category.entity.CategoryType;
import PoorToRich.PoorToRich.category.repository.CategoryRepository;
import PoorToRich.PoorToRich.category.request.CustomCategoryRequest;
import PoorToRich.PoorToRich.category.response.CategoryResponse;
import PoorToRich.PoorToRich.category.response.CustomCategoriesResponse;
import PoorToRich.PoorToRich.category.response.CustomCategoryResponse;
import PoorToRich.PoorToRich.category.response.DefaultCategoryResponse;
import PoorToRich.PoorToRich.global.exceptions.ConflictException;
import PoorToRich.PoorToRich.global.response.BaseResponse;
import PoorToRich.PoorToRich.global.response.Response;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

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

    public ResponseEntity<BaseResponse> createCategory(CustomCategoryRequest customCategory, CategoryType type) {
        if (isNameUsed(customCategory.getName())) {
            return BaseResponse.toResponseEntity(CategoryResponse.DUPLICATION_CATEGORY_NAME);
        }

        categoryRepository.save(buildCategory(customCategory, type));
        return BaseResponse.toResponseEntity(CategoryResponse.SUCCESS_CREATE_CATEGORY);
    }

    private Boolean isNameUsed(String name) {
        return categoryRepository.findAll()
                .stream()
                .anyMatch(category -> category.getName().equals(name));
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
