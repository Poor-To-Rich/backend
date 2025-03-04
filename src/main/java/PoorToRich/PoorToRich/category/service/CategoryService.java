package PoorToRich.PoorToRich.category.service;

import PoorToRich.PoorToRich.category.entity.Category;
import PoorToRich.PoorToRich.category.entity.CategoryType;
import PoorToRich.PoorToRich.category.repository.CategoryRepository;
import PoorToRich.PoorToRich.category.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getExpenseCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getType() == CategoryType.DEFAULT_EXPENSE)
                .map(category -> CategoryResponse.builder()
                        .name(category.getName())
                        .color(category.getColor())
                        .visibility(category.getVisibility())
                        .build())
                .collect(Collectors.toList());
    }
}
