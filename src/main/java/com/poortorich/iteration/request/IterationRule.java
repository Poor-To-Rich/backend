package com.poortorich.iteration.request;

import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.IterationRuleType;
import com.poortorich.iteration.validator.IterationRuleCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@IterationRuleCheck
public class IterationRule {

    @NotBlank(message = IterationResponseMessages.ITERATION_RULE_TYPE_REQUIRED)
    private String type;

    private List<String> daysOfWeek;

    @Valid
    private MonthlyOption monthlyOption;

    public IterationRuleType parseIterationType() {
        return IterationRuleType.from(type);
    }

    public List<Weekday> DaysOfWeekToList() {
        if (daysOfWeek == null) {
            return new ArrayList<>();
        }

        return daysOfWeek.stream()
                .map(Weekday::from)
                .toList();
    }

    public String parseDaysOfWeek() {
        if (daysOfWeek == null) {
            return null;
        }

        return daysOfWeek.stream()
                .map(Weekday::from)
                .map(Weekday::name)
                .collect(Collectors.joining(","));
    }
}
