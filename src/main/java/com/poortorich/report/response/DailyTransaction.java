package com.poortorich.report.response;

import com.poortorich.accountbook.response.AccountBookInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyTransaction {

    private String date;
    private Long countOfTransactions;
    private List<AccountBookInfoResponse> transactions;
}
