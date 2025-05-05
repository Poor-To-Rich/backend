package com.poortorich.iteration.util;

import com.poortorich.iteration.fixture.MonthlyOptionFixture;
import com.poortorich.iteration.request.MonthlyOption;

public class MonthlyOptionTestBuilder {

    private String mode = MonthlyOptionFixture.VALID_MODE;
    private Integer day = MonthlyOptionFixture.VALID_DAY;
    private Integer week = MonthlyOptionFixture.VALID_WEEK;
    private String dayOfWeek = MonthlyOptionFixture.VALID_DAY_OF_WEEK;

    public MonthlyOptionTestBuilder mode(String mode) {
        this.mode = mode;
        return this;
    }

    public MonthlyOptionTestBuilder day(Integer day) {
        this.day = day;
        return this;
    }

    public MonthlyOptionTestBuilder week(Integer week) {
        this.week = week;
        return this;
    }

    public MonthlyOptionTestBuilder dayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public MonthlyOption build() {
        return new MonthlyOption(
                mode,
                day,
                week,
                dayOfWeek
        );
    }

}
