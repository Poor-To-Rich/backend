package PoorToRich.PoorToRich.category.response;

import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultCategoriesResponse {
    private List<CategoryResponse> defaultCategories;
}
