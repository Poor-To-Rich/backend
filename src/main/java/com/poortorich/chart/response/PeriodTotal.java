package com.poortorich.chart.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PeriodTotal {

    private String period;
    private Long totalAmount;
    private String label;
}
