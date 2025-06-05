package com.poortorich.chart.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryLineResponse {

    private String period;
    private Long totalAmounts;
    private List<PeriodTotal> weeklyAmounts;
}
