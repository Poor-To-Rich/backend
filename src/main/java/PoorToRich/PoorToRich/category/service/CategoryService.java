package PoorToRich.PoorToRich.category.service;

import PoorToRich.PoorToRich.category.entity.CategoryType;
import PoorToRich.PoorToRich.category.repository.CategoryRepository;
import PoorToRich.PoorToRich.category.response.CustomCategoryResponse;
import PoorToRich.PoorToRich.category.response.DefaultCategoryResponse;
import lombok.RequiredArgsConstructor;
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
}
