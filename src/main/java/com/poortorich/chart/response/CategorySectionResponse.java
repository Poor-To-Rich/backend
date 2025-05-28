package com.poortorich.chart.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategorySectionResponse {
    private Long countOfLogs;
    private Boolean hasNext;
    private String nextCursor;
    private List<CategoryLog> categoryLogs;
}
