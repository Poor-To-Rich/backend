package com.poortorich.category.controller;

import com.poortorich.category.entity.CategoryType;
import com.poortorich.category.request.CategoryInfoRequest;
import com.poortorich.category.response.CategoryInfoResponse;
import com.poortorich.category.response.CustomCategoriesResponse;
import com.poortorich.category.response.CustomCategoryResponse;
import com.poortorich.category.response.DefaultCategoriesResponse;
import com.poortorich.category.response.DefaultCategoryResponse;
import com.poortorich.category.service.CategoryService;
import com.poortorich.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/expense/default")
    public ResponseEntity<DefaultCategoriesResponse> getExpenseDefaultCategories() {
        List<DefaultCategoryResponse> expenseCategories = categoryService.getDefaultCategories(CategoryType.DEFAULT_EXPENSE);
        DefaultCategoriesResponse response = new DefaultCategoriesResponse(expenseCategories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/income/default")
    public ResponseEntity<DefaultCategoriesResponse> getIncomeDefaultCategories() {
        List<DefaultCategoryResponse> incomeCategories = categoryService.getDefaultCategories(CategoryType.DEFAULT_INCOME);
        DefaultCategoriesResponse response = new DefaultCategoriesResponse(incomeCategories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expense/custom")
    public ResponseEntity<CustomCategoriesResponse> getExpenseCustomCategories() {
        List<CustomCategoryResponse> expenseCategories = categoryService.getCustomCategories(CategoryType.CUSTOM_EXPENSE);
        CustomCategoriesResponse response = new CustomCategoriesResponse(expenseCategories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/income/custom")
    public ResponseEntity<CustomCategoriesResponse> getIncomeCustomCategories() {
        List<CustomCategoryResponse> incomeCategories = categoryService.getCustomCategories(CategoryType.CUSTOM_INCOME);
        CustomCategoriesResponse response = new CustomCategoriesResponse(incomeCategories);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/expense")
    public ResponseEntity<BaseResponse> createExpenseCategory(@RequestBody @Valid CategoryInfoRequest categoryRequest) {
        return BaseResponse.toResponseEntity(categoryService.createCategory(categoryRequest, CategoryType.CUSTOM_EXPENSE));
    }

    @PostMapping("/income")
    public ResponseEntity<BaseResponse> createIncomeCategory(@RequestBody @Valid CategoryInfoRequest categoryRequest) {
        return BaseResponse.toResponseEntity(categoryService.createCategory(categoryRequest, CategoryType.CUSTOM_INCOME));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryInfoResponse> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<BaseResponse> modifyCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryInfoRequest categoryRequest) {
        return BaseResponse.toResponseEntity(categoryService.modifyCategory(categoryId, categoryRequest));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<BaseResponse> deleteCategory(@PathVariable Long categoryId) {
        return BaseResponse.toResponseEntity(categoryService.deleteCategory(categoryId));
    }
}
