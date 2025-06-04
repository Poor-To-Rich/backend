package com.poortorich.chart.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountBookBarResponse {

    private String differenceAmount;
    private String averageAmount;
    private List<PeriodTotal> totalAmounts;
}
