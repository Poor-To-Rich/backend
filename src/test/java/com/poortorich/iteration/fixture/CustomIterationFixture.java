package com.poortorich.iteration.fixture;

import com.poortorich.iteration.request.End;
import com.poortorich.iteration.request.IterationRule;
import com.poortorich.iteration.util.EndTestBuilder;
import com.poortorich.iteration.util.IterationRuleTestBuilder;

public class CustomIterationFixture {

    public static final IterationRule VALID_ITERATION_RULE = new IterationRuleTestBuilder().build();

    public static final Integer VALID_CYCLE = 1;

    public static final End VALID_END = new EndTestBuilder().build();

    private CustomIterationFixture() {}
}
