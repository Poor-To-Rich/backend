package com.poortorich.iteration.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomIterationInfoResponse {

    private IterationRuleInfoResponse iterationRule;
    private Integer cycle;
    private EndInfoResponse end;
}
