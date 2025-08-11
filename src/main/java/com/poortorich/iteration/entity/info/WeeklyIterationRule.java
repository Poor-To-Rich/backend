package com.poortorich.iteration.entity.info;

import com.poortorich.iteration.entity.enums.Weekday;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("WEEKLY")
public class WeeklyIterationRule extends IterationInfo {

    @Column(name = "days_of_week")
    private String daysOfWeek;

    @Transient
    public List<String> getDaysOfWeekList() {
        if (daysOfWeek == null || daysOfWeek.isBlank()) {
            return List.of();
        }

        return Arrays.stream(daysOfWeek.split(","))
                .map(Weekday::valueOf)
                .map(Weekday::toString)
                .toList();
    }
}
