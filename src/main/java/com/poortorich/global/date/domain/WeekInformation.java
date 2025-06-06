package com.poortorich.global.date.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class WeekInformation implements DateInfo {

    private final LocalDate startDate;
    private final LocalDate endDate;
}
