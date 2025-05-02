package com.poortorich.iteration.util;

import com.poortorich.iteration.fixture.EndFixture;
import com.poortorich.iteration.request.End;

public class EndTestBuilder {

    private String type = EndFixture.VALID_END_TYPE;
    private Integer count = EndFixture.VALID_END_COUNT;
    private String date = EndFixture.VALID_END_DATE;

    public EndTestBuilder type(String type) {
        this.type = type;
        return this;
    }

    public EndTestBuilder count(Integer count) {
        this.count = count;
        return this;
    }

    public EndTestBuilder date(String date) {
        this.date = date;
        return this;
    }

    public End build() {
        return new End(
                type,
                count,
                date
        );
    }
}
