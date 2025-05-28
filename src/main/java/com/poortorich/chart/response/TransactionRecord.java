package com.poortorich.chart.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionRecord {
    private Long id;
    private String title;
    private Long amount;
}
