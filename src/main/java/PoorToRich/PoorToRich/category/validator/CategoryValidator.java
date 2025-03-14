package PoorToRich.PoorToRich.category.validator;

import PoorToRich.PoorToRich.category.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public Boolean isNameUsed(String name) {
        return categoryRepository.findAll()
                .stream()
                .anyMatch(category -> category.getName().equals(name));
    }
}
