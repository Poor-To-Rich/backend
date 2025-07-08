package com.poortorich.iteration.request;

import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import com.poortorich.iteration.validator.annotations.MonthlyOptionCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@MonthlyOptionCheck
public class MonthlyOption {

    private String mode;

    private Integer day;

    private Integer week;

    private String dayOfWeek;

    public MonthlyMode parseMonthlyMode() {
        return MonthlyMode.from(mode);
    }

    public Weekday parseDayOfWeek() {
        return Weekday.from(dayOfWeek);
    }
}
