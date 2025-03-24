package com.poortorich.category.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultCategoriesResponse {
    private List<DefaultCategoryResponse> defaultCategories;
}
