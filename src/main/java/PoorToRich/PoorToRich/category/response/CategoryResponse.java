package PoorToRich.PoorToRich.category.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private String color;

    private String name;

    private boolean visibility;
}
