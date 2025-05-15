package com.poortorich.iteration.fixture;

import com.poortorich.iteration.request.MonthlyOption;
import com.poortorich.iteration.util.MonthlyOptionTestBuilder;

import java.util.List;

public class IterationRuleFixture {

    public static final String VALID_TYPE = "monthly";

    public static final List<String> VALID_DAYS_OF_WEEK = null;

    public static final MonthlyOption VALID_MONTHLY_OPTION = new MonthlyOptionTestBuilder().build();

    private IterationRuleFixture() {}
}
