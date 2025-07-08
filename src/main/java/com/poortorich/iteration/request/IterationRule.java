package com.poortorich.iteration.request;

import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.IterationRuleType;
import com.poortorich.iteration.validator.annotations.IterationRuleCheck;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.poortorich.iteration.entity.enums.Weekday.sortWeekday;

@Getter
@AllArgsConstructor
@IterationRuleCheck
public class IterationRule {

    private String type;

    private List<String> daysOfWeek;

    @Valid
    private MonthlyOption monthlyOption;

    public IterationRuleType parseIterationType() {
        return IterationRuleType.from(type);
    }

    public List<Weekday> daysOfWeekToList() {
        if (daysOfWeek == null) {
            return new ArrayList<>();
        }

        List<Weekday> weekdays = daysOfWeek.stream()
                .map(Weekday::from)
                .toList();

        return sortWeekday(weekdays);
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
