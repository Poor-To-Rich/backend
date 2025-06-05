package com.poortorich.chart.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryVerticalResponse {

    private String period;
    private Long totalAmount;
    private List<PeriodTotal> monthlyAmounts;
}
