package com.poortorich.iteration.mapper;

import com.poortorich.iteration.entity.enums.EndType;
import com.poortorich.iteration.entity.info.IterationInfo;
import com.poortorich.iteration.entity.info.MonthlyIterationRule;
import com.poortorich.iteration.entity.info.WeeklyIterationRule;
import com.poortorich.iteration.response.EndInfoResponse;
import com.poortorich.iteration.response.IterationRuleInfoResponse;
import com.poortorich.iteration.response.MonthlyOptionInfoResponse;
import org.hibernate.Hibernate;

public class IterationMapper {

    public static IterationRuleInfoResponse toIterationRuleInfo(IterationInfo info) {
        IterationInfo unproxiedInfo = (IterationInfo) Hibernate.unproxy(info);

        if (unproxiedInfo instanceof WeeklyIterationRule weekly) {
            return IterationRuleInfoResponse.builder()
                    .type(weekly.getIterationTypeLowerCase())
                    .daysOfWeek(weekly.getDaysOfWeekList())
                    .build();
        }

        if (unproxiedInfo instanceof MonthlyIterationRule monthly) {
            return IterationRuleInfoResponse.builder()
                    .type(monthly.getIterationTypeLowerCase())
                    .monthlyOption(toMonthlyOptionInfo(monthly))
                    .build();
        }

        return IterationRuleInfoResponse.builder()
                .type(info.getIterationTypeLowerCase())
                .build();
    }

    private static MonthlyOptionInfoResponse toMonthlyOptionInfo(MonthlyIterationRule monthly) {
        return MonthlyOptionInfoResponse.builder()
                .mode(monthly.getMonthlyMode().toString())
                .day(monthly.getMonthlyDay())
                .week(monthly.getMonthlyWeek())
                .dayOfWeek(monthly.getMonthlyDayOfWeek().toString())
                .build();
    }

    public static EndInfoResponse toEndInfo(IterationInfo info) {
        if (info.getEndType() == EndType.AFTER) {
            return EndInfoResponse.builder()
                    .type(info.getEndType().toString())
                    .count(info.getEndCount())
                    .build();
        }

        if (info.getEndType() == EndType.UNTIL) {
            return EndInfoResponse.builder()
                    .type(info.getEndType().toString())
                    .date(info.getEndDate())
                    .build();
        }

        return EndInfoResponse.builder()
                .type(info.getEndType().toString())
                .build();
    }
}
