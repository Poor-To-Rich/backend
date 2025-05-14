package com.poortorich.global.domain;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class YearInformation {

    private final Year year;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Map<Month, MonthInformation> months;
}
