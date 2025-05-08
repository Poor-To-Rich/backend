package com.poortorich.iteration.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IterationRuleInfoResponse {

    private String type;
    private List<String> daysOfWeek;
    private MonthlyOptionInfoResponse monthlyOption;
}
