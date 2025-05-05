package com.poortorich.iteration.util;

import com.poortorich.iteration.fixture.CustomIterationFixture;
import com.poortorich.iteration.request.CustomIteration;
import com.poortorich.iteration.request.End;
import com.poortorich.iteration.request.IterationRule;

public class CustomIterationTestBuilder {

    private IterationRule iterationRule = CustomIterationFixture.VALID_ITERATION_RULE;
    private Integer cycle = CustomIterationFixture.VALID_CYCLE;
    private End end = CustomIterationFixture.VALID_END;

    public CustomIterationTestBuilder iterationRule(IterationRule iterationRule) {
        this.iterationRule = iterationRule;
        return this;
    }

    public CustomIterationTestBuilder cycle(Integer cycle) {
        this.cycle = cycle;
        return this;
    }

    public CustomIterationTestBuilder end(End end) {
        this.end = end;
        return this;
    }

    public CustomIteration build() {
        return new CustomIteration(
                iterationRule,
                cycle,
                end
        );
    }
}
