package com.poortorich.iteration.request;

import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.IterationRuleType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class IterationRule {

    @NotBlank(message = IterationResponseMessages.ITERATION_RULE_TYPE_REQUIRED)
    private String type;

    private List<String> daysOfWeek;

    private MonthlyOption monthlyOption;

    public IterationRuleType parseIterationType() {
        return IterationRuleType.from(type);
    }

    public List<Weekday> DaysOfWeekToList() {
        if (daysOfWeek.isEmpty()) {
            return new ArrayList<>();
        }

        return daysOfWeek.stream()
                .map(Weekday::from)
                .toList();
    }

    public String parseDaysOfWeek() {
        if (daysOfWeek.isEmpty()) {
            return null;
        }

        return daysOfWeek.stream()
                .map(Weekday::from)
                .map(Weekday::name)
                .collect(Collectors.joining(","));
    }
}
