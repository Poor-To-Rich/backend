package com.poortorich.expense.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseInfoResponse {

    private LocalDate date;
    private String categoryName;
    private String title;
    private Long cost;
    private String paymentMethod;
    private String memo;
    private String iterationType;
    private CustomIterationInfoResponse customIteration;
}
