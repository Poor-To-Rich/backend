package com.poortorich.category.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultCategoryResponse {

    private Long id;
    private String color;
    private String name;
    private boolean visibility;
}
