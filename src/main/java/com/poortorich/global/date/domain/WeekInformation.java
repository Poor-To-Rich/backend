package com.poortorich.global.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class WeekInformation {

    private final LocalDate startDate;
    private final LocalDate endDate;
}
