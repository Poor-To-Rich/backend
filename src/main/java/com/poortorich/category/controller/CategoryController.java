package com.poortorich.category.controller;

import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.request.CategoryInfoRequest;
import com.poortorich.category.response.CategoryResponse;
import com.poortorich.category.response.CustomCategoriesResponse;
import com.poortorich.category.response.CustomCategoryResponse;
import com.poortorich.category.response.DefaultCategoriesResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.category.service.CategoryService;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/expense/default")
    public ResponseEntity<BaseResponse> getExpenseDefaultCategories(@AuthenticationPrincipal User user) {
        List<DefaultCategoryResponse> expenseCategories
                = categoryService.getDefaultCategories(CategoryType.DEFAULT_EXPENSE, user);
        DefaultCategoriesResponse response = new DefaultCategoriesResponse(expenseCategories);
        return DataResponse.toResponseEntity(CategoryResponse.GET_DEFAULT_EXPENSE_CATEGORIES_SUCCESS, response);
    }

    @GetMapping("/income/default")
    public ResponseEntity<BaseResponse> getIncomeDefaultCategories(@AuthenticationPrincipal User user) {
        List<DefaultCategoryResponse> incomeCategories
                = categoryService.getDefaultCategories(CategoryType.DEFAULT_INCOME, user);
        DefaultCategoriesResponse response = new DefaultCategoriesResponse(incomeCategories);
        return DataResponse.toResponseEntity(CategoryResponse.GET_DEFAULT_INCOME_CATEGORIES_SUCCESS, response);
    }

    @GetMapping("/expense/custom")
    public ResponseEntity<BaseResponse> getExpenseCustomCategories(@AuthenticationPrincipal User user) {
        List<CustomCategoryResponse> expenseCategories
                = categoryService.getCustomCategories(CategoryType.CUSTOM_EXPENSE, user);
        CustomCategoriesResponse response = new CustomCategoriesResponse(expenseCategories);
        return DataResponse.toResponseEntity(CategoryResponse.GET_CUSTOM_EXPENSE_CATEGORIES_SUCCESS, response);
    }

    @GetMapping("/income/custom")
    public ResponseEntity<BaseResponse> getIncomeCustomCategories(@AuthenticationPrincipal User user) {
        List<CustomCategoryResponse> incomeCategories
                = categoryService.getCustomCategories(CategoryType.CUSTOM_INCOME, user);
        CustomCategoriesResponse response = new CustomCategoriesResponse(incomeCategories);
        return DataResponse.toResponseEntity(CategoryResponse.GET_CUSTOM_INCOME_CATEGORIES_SUCCESS, response);
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse> getActiveCategories(@RequestParam String type, @AuthenticationPrincipal User user) {
        return DataResponse.toResponseEntity(
                CategoryResponse.GET_ACTIVE_CATEGORIES_SUCCESS,
                categoryService.getActiveCategories(type, user)
        );
    }

    @PostMapping("/expense")
    public ResponseEntity<BaseResponse> createExpenseCategory(
            @RequestBody @Valid CategoryInfoRequest categoryRequest,
            @AuthenticationPrincipal User user) {
        return BaseResponse.toResponseEntity(
                categoryService.createCategory(categoryRequest, CategoryType.CUSTOM_EXPENSE, user)
        );
    }

    @PostMapping("/income")
    public ResponseEntity<BaseResponse> createIncomeCategory(
            @RequestBody @Valid CategoryInfoRequest categoryRequest,
            @AuthenticationPrincipal User user) {
        return BaseResponse.toResponseEntity(
                categoryService.createCategory(categoryRequest, CategoryType.CUSTOM_INCOME, user)
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<BaseResponse> getCategory(@PathVariable Long categoryId, @AuthenticationPrincipal User user) {
        return DataResponse.toResponseEntity(
                CategoryResponse.GET_CUSTOM_CATEGORY_SUCCESS,
                categoryService.getCategory(categoryId, user)
        );
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<BaseResponse> modifyCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryInfoRequest categoryRequest, @AuthenticationPrincipal User user) {
        return BaseResponse.toResponseEntity(categoryService.modifyCategory(categoryId, categoryRequest, user));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<BaseResponse> deleteCategory(@PathVariable Long categoryId, @AuthenticationPrincipal User user) {
        return BaseResponse.toResponseEntity(categoryService.deleteCategory(categoryId, user));
    }
}
