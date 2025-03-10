package PoorToRich.PoorToRich.category.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomCategoriesResponse {
    private List<CustomCategoryResponse> customCategories;
}
