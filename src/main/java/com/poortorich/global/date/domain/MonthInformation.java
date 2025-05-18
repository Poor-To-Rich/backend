package com.poortorich.global.date.domain;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MonthInformation {

    private final YearMonth yearMonth;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<WeekInformation> weeks;
}
