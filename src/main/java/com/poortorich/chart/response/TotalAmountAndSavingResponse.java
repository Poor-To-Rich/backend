package com.poortorich.chart.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalAmountAndSavingResponse {

    private Long savingCategoryId;
    private Long totalAmount;
    private Long totalSaving;
}
