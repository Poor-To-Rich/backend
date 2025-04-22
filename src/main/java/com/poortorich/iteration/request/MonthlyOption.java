package com.poortorich.iteration.request;

import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyOption {

    private String mode;

    private int day;

    private int week;

    private String dayOfWeek;

    public MonthlyMode parseMonthlyMode() {
        return MonthlyMode.from(mode);
    }

    public Weekday parseDayOfWeek() {
        return Weekday.from(dayOfWeek);
    }
}
