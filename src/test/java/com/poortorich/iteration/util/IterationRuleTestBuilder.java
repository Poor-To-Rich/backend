package com.poortorich.iteration.util;

import com.poortorich.iteration.fixture.IterationRuleFixture;
import com.poortorich.iteration.request.IterationRule;
import com.poortorich.iteration.request.MonthlyOption;

import java.util.List;

public class IterationRuleTestBuilder {

    private String type = IterationRuleFixture.VALID_TYPE;
    private List<String> daysOfWeek = IterationRuleFixture.VALID_DAYS_OF_WEEK;
    private MonthlyOption monthlyOption = IterationRuleFixture.VALID_MONTHLY_OPTION;

    public IterationRuleTestBuilder type(String type) {
        this.type = type;
        return this;
    }

    public IterationRuleTestBuilder daysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
        return this;
    }

    public IterationRuleTestBuilder monthlyOption(MonthlyOption monthlyOption) {
        this.monthlyOption = monthlyOption;
        return this;
    }

    public IterationRule build() {
        return new IterationRule(
                type,
                daysOfWeek,
                monthlyOption
        );
    }
}
