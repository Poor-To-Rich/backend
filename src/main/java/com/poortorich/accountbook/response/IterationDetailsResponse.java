package com.poortorich.accountbook.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IterationDetailsResponse {

    private Long totalAmount;
    private List<AccountBookInfoResponse> iterationAccountBooks;
}
