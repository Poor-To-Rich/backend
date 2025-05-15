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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<BaseResponse> getExpenseDefaultCategories(@AuthenticationPrincipal UserDetails userDetails) {
        List<DefaultCategoryResponse> expenseCategories
                = categoryService.getDefaultCategories(CategoryType.DEFAULT_EXPENSE, userDetails.getUsername());
        DefaultCategoriesResponse response = new DefaultCategoriesResponse(expenseCategories);
        return DataResponse.toResponseEntity(CategoryResponse.GET_DEFAULT_EXPENSE_CATEGORIES_SUCCESS, response);
    }

    @GetMapping("/income/default")
    public ResponseEntity<BaseResponse> getIncomeDefaultCategories(@AuthenticationPrincipal UserDetails userDetails) {
        List<DefaultCategoryResponse> incomeCategories
                = categoryService.getDefaultCategories(CategoryType.DEFAULT_INCOME, userDetails.getUsername());
        DefaultCategoriesResponse response = new DefaultCategoriesResponse(incomeCategories);
        return DataResponse.toResponseEntity(CategoryResponse.GET_DEFAULT_INCOME_CATEGORIES_SUCCESS, response);
    }

    @GetMapping("/expense/custom")
    public ResponseEntity<BaseResponse> getExpenseCustomCategories(@AuthenticationPrincipal UserDetails userDetails) {
        List<CustomCategoryResponse> expenseCategories
                = categoryService.getCustomCategories(CategoryType.CUSTOM_EXPENSE, userDetails.getUsername());
        CustomCategoriesResponse response = new CustomCategoriesResponse(expenseCategories);
        return DataResponse.toResponseEntity(CategoryResponse.GET_CUSTOM_EXPENSE_CATEGORIES_SUCCESS, response);
    }

    @GetMapping("/income/custom")
    public ResponseEntity<BaseResponse> getIncomeCustomCategories(@AuthenticationPrincipal UserDetails userDetails) {
        List<CustomCategoryResponse> incomeCategories
                = categoryService.getCustomCategories(CategoryType.CUSTOM_INCOME, userDetails.getUsername());
        CustomCategoriesResponse response = new CustomCategoriesResponse(incomeCategories);
        return DataResponse.toResponseEntity(CategoryResponse.GET_CUSTOM_INCOME_CATEGORIES_SUCCESS, response);
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse> getActiveCategories(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String type) {
        return DataResponse.toResponseEntity(
                CategoryResponse.GET_ACTIVE_CATEGORIES_SUCCESS,
                categoryService.getActiveCategories(type, userDetails.getUsername())
        );
    }

    @PostMapping("/expense")
    public ResponseEntity<BaseResponse> createExpenseCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CategoryInfoRequest categoryRequest) {
        return BaseResponse.toResponseEntity(
                categoryService.createCategory(categoryRequest, CategoryType.CUSTOM_EXPENSE, userDetails.getUsername())
        );
    }

    @PostMapping("/income")
    public ResponseEntity<BaseResponse> createIncomeCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CategoryInfoRequest categoryRequest) {
        return BaseResponse.toResponseEntity(
                categoryService.createCategory(categoryRequest, CategoryType.CUSTOM_INCOME, userDetails.getUsername())
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<BaseResponse> getCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId) {
        return DataResponse.toResponseEntity(
                CategoryResponse.GET_CUSTOM_CATEGORY_SUCCESS,
                categoryService.getCategory(categoryId, userDetails.getUsername())
        );
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<BaseResponse> modifyCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryInfoRequest categoryRequest) {
        return BaseResponse.toResponseEntity(
                categoryService.modifyCategory(categoryId, categoryRequest, userDetails.getUsername())
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<BaseResponse> deleteCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId) {
        return BaseResponse.toResponseEntity(categoryService.deleteCategory(categoryId, userDetails.getUsername()));
    }
}
