package com.poortorich.income.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poortorich.accountbook.response.InfoResponse;
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
public class IncomeInfoResponse implements InfoResponse {

    private LocalDate date;
    private String categoryName;
    private String title;
    private Long cost;
    private String memo;
    private String iterationType;
    private CustomIterationInfoResponse customIteration;
}
