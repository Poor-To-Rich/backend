package com.poortorich.chart.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryLog {

    private String date;
    private Long countOfTransactions;
    private List<TransactionRecord> transactions;
}
