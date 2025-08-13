package com.poortorich.transaction.response;

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
public class DailyDetailsResponse {

    private Long totalAmount;
    private Long totalExpense;
    private Long totalIncome;
    private List<AccountBookInfoResponse> dailyDetails;
}
