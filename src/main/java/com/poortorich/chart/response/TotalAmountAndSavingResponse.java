package com.poortorich.chart.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalExpenseAndSavingResponse {

    private Long savingCategoryId;
    private Long totalExpenseAmount;
    private Long totalSavingAmount;
}
