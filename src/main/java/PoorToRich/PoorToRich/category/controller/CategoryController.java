package PoorToRich.PoorToRich.category.controller;

import PoorToRich.PoorToRich.category.response.DefaultCategoriesResponse;
import PoorToRich.PoorToRich.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/expense/default")
    public DefaultCategoriesResponse getExpenseDefaultCategories() {
        return new DefaultCategoriesResponse(categoryService.getExpenseCategories());
    }

    @GetMapping("/income/default")
    public DefaultCategoriesResponse getIncomeDefaultCategories() {
        return new DefaultCategoriesResponse(categoryService.getIncomeCategories());
    }
}
