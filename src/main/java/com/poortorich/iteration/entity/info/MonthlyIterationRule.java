package com.poortorich.iteration.entity.info;

import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DiscriminatorValue("MONTHLY")
public class MonthlyIterationRule extends IterationInfo {

    @Enumerated(EnumType.STRING)
    @Column(name = "monthly_mode")
    private MonthlyMode monthlyMode;

    @Column(name = "monthly_day")
    private Integer monthlyDay;

    @Column(name = "monthly_week")
    private Integer monthlyWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "monthly_day_of_week")
    private Weekday monthlyDayOfWeek;
}
