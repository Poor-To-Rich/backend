package com.poortorich.iteration.request;

import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.constants.IterationValidationConstraints;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomIteration {

    @Valid
    @NotNull(message = IterationResponseMessages.ITERATION_RULE_REQUIRED)
    private IterationRule iterationRule;

    @NotNull(message = IterationResponseMessages.CYCLE_REQUIRED)
    @Min(value = IterationValidationConstraints.CYCLE_MIN,
            message = IterationResponseMessages.CYCLE_TOO_SMALL)
    @Max(value = IterationValidationConstraints.CYCLE_MAX,
            message = IterationResponseMessages.CYCLE_TOO_BIG)
    private Integer cycle;

    @Valid
    @NotNull(message = IterationResponseMessages.END_REQUIRED)
    private End end;
}
