package PoorToRich.PoorToRich.category.controller;

import PoorToRich.PoorToRich.category.entity.CategoryType;
import PoorToRich.PoorToRich.category.response.CustomCategoriesResponse;
import PoorToRich.PoorToRich.category.response.CustomCategoryResponse;
import PoorToRich.PoorToRich.category.response.DefaultCategoriesResponse;
import PoorToRich.PoorToRich.category.response.DefaultCategoryResponse;
import PoorToRich.PoorToRich.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        List<CustomCategoryResponse> incomeCategories = categoryService.getCustomCategories(CategoryType.CUSTOM_EXPENSE);
        CustomCategoriesResponse response = new CustomCategoriesResponse(incomeCategories);
        return ResponseEntity.ok(response);
    }
}
