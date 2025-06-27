package com.poortorich.chart.response;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryChartResponse {

    List<Map<String, Double>> aggregatedData;
    Map<String, String> categoryColors;
    List<CategoryChart> categoryCharts;
}
